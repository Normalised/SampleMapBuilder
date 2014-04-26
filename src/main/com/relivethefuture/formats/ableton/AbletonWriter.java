package com.relivethefuture.formats.ableton;

import com.relivethefuture.ResourceUtils;
import com.relivethefuture.generic.Instrument;
import com.relivethefuture.generic.Sample;
import com.relivethefuture.generic.Zone;
import com.relivethefuture.kitbuilder.writers.BasicInstrumentWriter;
import net.sf.jtpl.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.GZIPOutputStream;

/**
 * Created by martin on 23/01/12 at 13:40
 */
public class AbletonWriter extends BasicInstrumentWriter {

    private final Logger logger = LoggerFactory.getLogger(AbletonWriter.class);

    protected String itemTemplate = "";
    protected String mainTemplate = "";
    private Template rackEntryTemplate;
    private Template wrapperTemplate;
    private Template multiSamplePartTemplate;

    private boolean isWindows;
    private boolean isOSX;

    private OSXFileDataGenerator osxFileDataGenerator;

    public AbletonWriter() {
        super();
        String osName = System.getProperty("os.name").toLowerCase();
        isWindows = osName.startsWith("win");
        isOSX = osName.startsWith("mac os x");
//        if(isOSX) {
//            osxFileDataGenerator = new OSXFileDataGenerator();
//        }
    }

    protected void setupTemplates() {
        try {
            if(itemTemplate != null && itemTemplate.length() > 0) {
                logger.debug("Setup rack entry template " + itemTemplate);
                rackEntryTemplate = getTemplate(itemTemplate);
            }

            wrapperTemplate = getTemplate(mainTemplate);
            multiSamplePartTemplate = getTemplate("ableton/sections/MultiSamplePart.xml");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void write(Instrument instrument, File outputDir) {
        // Generate all zones
        File outputFile = new File(outputDir, instrument.getName());
        try {
            String out = generateXml(instrument, outputDir);
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(new FileOutputStream(outputFile));
            gzipOutputStream.write(out.getBytes());
            gzipOutputStream.close();
            // DEBUG
            FileWriter fileWriter = new FileWriter(new File(outputDir, instrument.getName() + ".xml"));
            fileWriter.write(out);
            fileWriter.close();

        } catch (IOException e3) {
            logger.error("IO Exception reading template file " + e3.getMessage());
        }
    }

    public String generateXml(Instrument instrument, File outputDir) throws IOException {

        wrapperTemplate.reset();

        Sample sample;
        File file;
        //Path path;
        // name, filename, relativePath0,1,2,3, filesize, crc, duration
        // <RelativePathElement Dir="${RelativePath0}" />
        StringBuilder zones = new StringBuilder();

        String msptOut = null;
        String retOut = null;

        for(Zone zone : instrument.getZones()) {

            logger.debug("Generating zone " + zone.getSample().getFile().getName());
            sample = zone.getSample();
            file = sample.getFile();
            String relativePath = createRelativePath(sample, outputDir);

            try {
                Integer receivingNote = (128 - zone.getNoteRange().getLow());

                multiSamplePartTemplate.assign("relativePaths",relativePath);

                multiSamplePartTemplate.assign("filename",file.getName());
                multiSamplePartTemplate.assign("name",file.getName());
                multiSamplePartTemplate.assign("filesize",String.valueOf(file.length()));
                multiSamplePartTemplate.assign("duration",sample.getDuration().toString());

                multiSamplePartTemplate.assign("keymin",zone.getNoteRange().getLow().toString());
                multiSamplePartTemplate.assign("keymax",zone.getNoteRange().getHigh().toString());
                multiSamplePartTemplate.assign("rootkey",sample.getRoot().toString());
                isWindows = false;
                if(isWindows) {
                    multiSamplePartTemplate.assign("fileRefData",getFileRefData(file));
                } else {
                    multiSamplePartTemplate.assign("fileRefData","");
                }

                //rackEntryTemplate.assign("crc",sample.getCrc().toString());
                // TODO : find out how to do 14 bit CRC?
                multiSamplePartTemplate.assign("crc","12345");
                multiSamplePartTemplate.parse("main");
                msptOut = multiSamplePartTemplate.out();
                if(rackEntryTemplate != null) {
                    rackEntryTemplate.assign("name", file.getName());
                    rackEntryTemplate.assign("keymin", zone.getNoteRange().getLow().toString());
                    rackEntryTemplate.assign("receivingNote", receivingNote.toString());
                    rackEntryTemplate.assign("multiSampleParts",msptOut);
                    rackEntryTemplate.parse("main");
                    retOut = rackEntryTemplate.out();
                    //logger.debug("RET : " + retOut);
                    zones.append(retOut);
                    rackEntryTemplate.reset();
                } else {
                    zones.append(msptOut);
                }

                multiSamplePartTemplate.reset();

            } catch(UnsupportedAudioFileException e) {
                logger.warn("Unsupported Audio File : " + e.getMessage());
            } catch(IOException e2) {
                logger.warn("IO Exception : " + e2.getMessage());
            } catch(Exception e3) {
                logger.warn("E3 : " + e3.getMessage());
            }
        }
        wrapperTemplate.assign("name",instrument.getName());
        wrapperTemplate.assign("items", zones.toString());
        wrapperTemplate.parse("main");
        return wrapperTemplate.out();
    }

    private String createRelativePath(Sample sample, File outputDir) {
        StringBuilder relativePathSection = new StringBuilder();

        //Path relative = outputDir.toPath().relativize(sample.getFile().getParentFile().toPath());

        ArrayList<String> relativePath = ResourceUtils.getRelativePathSections(outputDir,sample.getFile().getParentFile());
        for(String element:relativePath) {
            if(element.equals("..")) {
                element = "";
            }
            relativePathSection.append("                    <RelativePathElement Dir=\"").append(element).append("\" />\n");
        }
        return relativePathSection.toString();
    }

    private String getFileRefData(File file) {
        if(isOSX) {
            //return osxFileDataGenerator.createOSXFileData(file, false);
        }
        StringBuilder data = new StringBuilder("<Data>\n");
        String utfByteString = pathToUTF16ByteString(file.getAbsolutePath());
        data.append(utfByteString);
        data.append("</Data>");
        return data.toString();
    }

    private String pathToUTF16ByteString(String path) {
        byte[] bytes = path.getBytes(Charset.forName("UTF-16"));
        StringBuilder hexString = new StringBuilder();
        if(bytes.length < 4) {
            logger.warn("Path doesnt have enough bytes " + bytes.length);
            return hexString.toString();
        }
        for (int i = 3; i < bytes.length; i++) {
            byte aByte = bytes[i];
            hexString.append(String.format("%02X", aByte));
        }
        hexString.append("000000");
        return hexString.toString();
    }

}
