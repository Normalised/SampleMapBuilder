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
public class RenoiseFlacWriter extends BasicInstrumentWriter {

    private final Logger logger = LoggerFactory.getLogger(RenoiseFlacWriter.class);
    private FLAC_FileEncoder fileEncoder;
    private ClassLoader classLoader;

    public RenoiseFlacWriter() {
        classLoader = Thread.currentThread().getContextClassLoader();
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
            File tmpFlac = new File("tmp.flac");
            logger.debug("Creating flac encoder");
            if (fileEncoder == null) {
                fileEncoder = new FLAC_FileEncoder();
            }

            fileEncoder.getStreamConfiguration().setMaxBlockSize(512);

            for (Zone zone : instrument.getZones()) {

                logger.debug("Writing " + zone.getSample().getFile().getName());

                FLAC_FileEncoder.Status status = fileEncoder.encode(zone.getSample().getFile(), tmpFlac);
                if (status == FLAC_FileEncoder.Status.INTERNAL_ERROR) {
                    logger.warn("Error converting " + zone.getSample().getFile().getAbsolutePath());
                    continue;
                }
                // Add ZIP entry to output stream.
                if (s < 10) {
                    sampleNumber = "0" + s.toString();
                } else {
                    sampleNumber = s.toString();
                }

                String[] parts = getFilenameAndExtension(zone.getSample().getFile());
                zos.putNextEntry(new ZipEntry("SampleData/Sample" + sampleNumber + " (" + parts[0] + ").flac"));

                FileInputStream in = new FileInputStream(tmpFlac);
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
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Template sampleTemplate = getTemplate("renoise/sections/SampleTemplate.xml");
        Template zoneTemplate = getTemplate("renoise/sections/ZoneTemplate.xml");

        Sample sample;
        File file;
        //Path path;
        // name, filename, relativePath0,1,2,3, filesize, crc, duration
        // <RelativePathElement Dir="${RelativePath0}" />
        String zones = "";
        String samples = "";
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
                samples += sampleOut;
                zones += zoneOut;
                sampleTemplate.reset();
                zoneTemplate.reset();
                sampleIndex++;
            } catch (UnsupportedAudioFileException e) {
                continue;
            } catch (IOException e2) {
                continue;
            }
        }
        //logger.debug(zones);
        Template mainTemplate = getTemplate("renoise/InstrumentTemplate.xml");
        mainTemplate.assign("samples", samples);
        mainTemplate.assign("zones", zones);
        mainTemplate.assign("name", instrument.getName());
        mainTemplate.parse("main");
        return mainTemplate.out();
    }

}
