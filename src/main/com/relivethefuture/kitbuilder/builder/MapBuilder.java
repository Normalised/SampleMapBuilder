package com.relivethefuture.kitbuilder.builder;

import com.relivethefuture.generic.*;
import com.relivethefuture.kitbuilder.KitFileNameGenerator;
import com.relivethefuture.kitbuilder.model.FolderOfSamples;
import com.relivethefuture.kitbuilder.output.OutputConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MapBuilder {

    private final Logger logger = LoggerFactory.getLogger(MapBuilder.class);
    
    private List<com.relivethefuture.generic.Instrument> instruments;

    private InstrumentFactory instrumentFactory;

    private ZoneFactory zoneFactory;
    private OutputConfig currentConfig;
    private KitFileNameGenerator nameGenerator;

    public MapBuilder() {

    }

    public List<Instrument> build(OutputConfig config, KitFileNameGenerator fileNameGenerator, List<FolderOfSamples> items) {

        currentConfig = config;
        nameGenerator = fileNameGenerator;
        instruments = new ArrayList<Instrument>();
        instrumentFactory = config.getInstrumentFactory();
        zoneFactory = config.getZoneFactory();

        int total = items.size();
        int current = 0;

        logger.debug("Building " + total + " items");
        for (FolderOfSamples item : items) {
            logger.debug("Fos with " + item.samples.size() + " samples.");
            if(item.samples.size() > 0) {
                createInstrument(item, config);
            }
            current++;
        }
        return instruments;
    }

    private void createInstrument(FolderOfSamples folderOfSamples, OutputConfig config) {

        logger.debug("Create Instrument " + folderOfSamples.getSourceDirectory().getAbsolutePath() + " : " + config.getFormat().type());
        String extra = "";
        Integer listID = 1;

        boolean hasMultiple = false;

        List<List<Zone>> zoneLists = getZoneLists(folderOfSamples);

        logger.debug("Zone Lists " + zoneLists.size());
        if(zoneLists.size() > 1) {
            hasMultiple = true;
        }
        for(List<Zone> zones : zoneLists) {

            if(hasMultiple) {
                extra = listID.toString();
            }
            String fileName = nameGenerator.generateFileName(folderOfSamples, extra);

            logger.debug("Create Map " + fileName + " : " + folderOfSamples.getDirectory().getAbsolutePath() + " : " + zones.size());

            if (zones.size() > 128) {
                logger.warn("Too many files in " + folderOfSamples.getDirectory().getAbsolutePath());
                zones = zones.subList(0, 128);
            }

            Instrument instrument = instrumentFactory.createInstrument(fileName);
            instrument.setZones(zones);
            instrument.setSource(folderOfSamples);
            instruments.add(instrument);
            listID++;
        }
    }

    private List<List<Zone>> getZoneLists(FolderOfSamples folderOfSamples) {

        logger.debug("Get Zone Lists " + folderOfSamples.samples.size());
        // get a list of samples and remove 'excluded'

        // split into groups of 128 and create zones
        ArrayList<Sample> samples = folderOfSamples.samples;
        // remove excluded samples

        List<List<Zone>> zoneLists = new ArrayList<List<Zone>>();

        List<Sample> currentSamples = new ArrayList<Sample>();
        for(Sample sample : samples) {
            if(!sample.getExclude()) {
                currentSamples.add(sample);
            }

            if(currentSamples.size() == 127) {
                // create zones, start new list
                zoneLists.add(createZones(currentSamples));
                currentSamples.clear();
            }
        }

        if(currentSamples.size() > 0) {
            zoneLists.add(createZones(currentSamples));
        }
        return zoneLists;
    }

    private List<Zone> createZones(List<Sample> samples) {
        List<Zone> zones = new ArrayList<Zone>(samples.size());
        int lowkey = 0;
        for(Sample sample : samples) {
            Zone zone = zoneFactory.createZone();
            zone.setSample(sample);
            zone.setKeyRange(lowkey, lowkey);

            if(currentConfig.useFixedRoot()) {
                sample.setRoot(currentConfig.getRoot());
            } else {
                sample.setRoot(lowkey);
            }

            lowkey++;

            zones.add(zone);
        }
        return zones;
    }
}
