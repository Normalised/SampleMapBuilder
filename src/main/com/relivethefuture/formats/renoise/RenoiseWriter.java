package com.relivethefuture.formats.renoise;

import com.relivethefuture.generic.Instrument;
import com.relivethefuture.generic.Sample;
import com.relivethefuture.generic.Zone;
import com.relivethefuture.kitbuilder.writers.BasicInstrumentWriter;
import javaFlacEncoder.FLAC_FileEncoder;
import net.sf.jtpl.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by martin on 23/01/12 at 13:40
 */
public class RenoiseWriter extends BasicInstrumentWriter {

    private final Logger logger = LoggerFactory.getLogger(RenoiseWriter.class);
    private FLAC_FileEncoder fileEncoder;
    private Template sampleTemplate;
    private Template zoneTemplate;
    private Template mainTemplate;

    public RenoiseWriter() {
        super();
        try {
            sampleTemplate = getTemplate("renoise/sections/SampleTemplate.xml");
            zoneTemplate = getTemplate("renoise/sections/ZoneTemplate.xml");
            mainTemplate = getTemplate("renoise/InstrumentTemplate.xml");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public void write(Instrument instrument, File outputDir) {
        logger.debug("Writing Renoise Instrument to " + outputDir.getAbsolutePath());
        try {
            File outputFile = new File(outputDir, instrument.getName());
            if (outputFile.exists()) {
                outputFile.delete();
            }
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputFile));
            zos.putNextEntry(new ZipEntry("SampleData/"));
            Integer s = 0;
            String sampleNumber = "";
            byte[] buf = new byte[1024];

            for (Zone zone : instrument.getZones()) {

                File sampleFile = zone.getSample().getFile();
                //logger.debug("Writing " + sampleFile.getName());

                // Add ZIP entry to output stream.
                if (s < 10) {
                    sampleNumber = "0" + s.toString();
                } else {
                    sampleNumber = s.toString();
                }

                String[] parts = getFilenameAndExtension(sampleFile);
                zos.putNextEntry(new ZipEntry("SampleData/Sample" + sampleNumber + " (" + parts[0] + ")." + parts[1]));

                FileInputStream in = new FileInputStream(sampleFile);
                int len;
                while ((len = in.read(buf)) > 0) {
                    zos.write(buf, 0, len);
                }

                // Complete the entry
                s++;
                zos.closeEntry();
                in.close();
            }
            zos.putNextEntry(new ZipEntry("Instrument.xml"));
            logger.debug("Generating XML");
            String xml = generateXml(instrument);
            logger.debug("Write XML bytes to zip");
            zos.write(xml.getBytes(Charset.forName("UTF-8")));
            zos.close();
        } catch (IOException e) {
            logger.error("Couldnt write renoise xml to " + outputDir.getAbsolutePath());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        logger.debug("Instrument write complete");
    }

    public String generateXml(Instrument instrument) throws IOException {

        logger.debug("Got templates");
        Sample sample;
        File file;
        //Path path;
        // name, filename, relativePath0,1,2,3, filesize, crc, duration
        // <RelativePathElement Dir="${RelativePath0}" />
        StringBuffer zones = new StringBuffer();
        StringBuffer samples = new StringBuffer();
        Integer sampleIndex = 0;
        for (Zone zone : instrument.getZones()) {

            logger.debug("Generating zone " + zone.getSample().getFile().getName());
            sample = zone.getSample();
            file = sample.getFile();

            try {
                sampleTemplate.assign("name", file.getName());
                // //File:C:\Program Files\Renoise 2.8.0\Samples\Kicks\Kick03.flac
                sampleTemplate.assign("filename", file.getName());
                sampleTemplate.assign("duration", sample.getDuration().toString());

                zoneTemplate.assign("index", sampleIndex.toString());
                zoneTemplate.assign("rootkey", sample.getRoot().toString());
                zoneTemplate.assign("keymin", zone.getNoteRange().getLow().toString());
                zoneTemplate.assign("keymax", zone.getNoteRange().getHigh().toString());

                sampleTemplate.parse("main");
                zoneTemplate.parse("main");
                String sampleOut = sampleTemplate.out();
                String zoneOut = zoneTemplate.out();
                //logger.debug("Template " + out);
                samples.append(sampleOut);
                zones.append(zoneOut);
                sampleTemplate.reset();
                zoneTemplate.reset();
                sampleIndex++;
            } catch (UnsupportedAudioFileException e) {
                logger.warn("Unsupported audio file");
            } catch (IOException e2) {
                logger.warn("IO Exception " + e2.getMessage());
            } catch(Exception e) {
                logger.warn("Exception " + e.getMessage());
            }
        }
        logger.debug("Filling main template");
        mainTemplate.reset();
        mainTemplate.assign("samples", samples.toString());
        mainTemplate.assign("zones", zones.toString());
        mainTemplate.assign("name", instrument.getName());
        mainTemplate.parse("main");
        return mainTemplate.out();
    }

}
