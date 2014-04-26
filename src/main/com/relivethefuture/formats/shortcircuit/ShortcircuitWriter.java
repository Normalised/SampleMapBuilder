package com.relivethefuture.formats.shortcircuit;

import com.relivethefuture.ResourceUtils;
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

/**
 * filename format : d:\audio\samples\tinysilence.wav
 */
public class ShortcircuitWriter extends BasicInstrumentWriter {

    private final Logger logger = LoggerFactory.getLogger(ShortcircuitWriter.class);
    private Template zoneTemplate;
    private Template mainTemplate;
    private File instrumentFile;

    public ShortcircuitWriter() {
        super();
        try {
            zoneTemplate = getTemplate("shortcircuit/ZoneTemplate.xml");
            mainTemplate = getTemplate("shortcircuit/InstrumentTemplate.xml");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    public void write(Instrument instrument, File outputDirectory) {
        try {
            instrumentFile = new File(outputDirectory,instrument.getName());
            FileWriter fileWriter = new FileWriter(new File(outputDirectory, instrument.getName()));
            fileWriter.write(generateXml(instrument));
            fileWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String generateXml(Instrument instrument) throws IOException {

        Sample sample;
        File file;
        StringBuffer zones = new StringBuffer();
        Integer sampleIndex = 0;
        File samplesDirectory = instrument.getSamplesDirectory();
        for (Zone zone : instrument.getZones()) {

            logger.debug("Generating zone " + zone.getSample().getFile().getName());
            sample = zone.getSample();
            file = sample.getFile();

            try {
                zoneTemplate.assign("name", file.getName());
                zoneTemplate.assign("filename", ResourceUtils.getRelativePath(instrumentFile,file));
                zoneTemplate.assign("duration", sample.getDuration().toString());
                zoneTemplate.assign("index", sampleIndex.toString());
                zoneTemplate.assign("rootkey", sample.getRoot().toString());
                zoneTemplate.assign("keymin", zone.getNoteRange().getLow().toString());
                zoneTemplate.assign("keymax", zone.getNoteRange().getHigh().toString());

                zoneTemplate.parse("main");
                String zoneOut = zoneTemplate.out();
                //logger.debug("Template " + out);
                zones.append(zoneOut);
                zoneTemplate.reset();
                sampleIndex++;
            } catch (UnsupportedAudioFileException e) {
                continue;
            } catch (IOException e2) {
                continue;
            }
        }
        logger.debug("Filling main template");
        mainTemplate.reset();
        mainTemplate.assign("zones", zones.toString());
        mainTemplate.assign("name", instrument.getName());
        mainTemplate.parse("main");
        return mainTemplate.out();
    }
}
