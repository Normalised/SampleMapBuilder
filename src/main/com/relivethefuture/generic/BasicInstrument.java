package com.relivethefuture.generic;

import com.relivethefuture.kitbuilder.model.FolderOfSamples;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by martin on 08/01/12 at 15:02
 */
public class BasicInstrument implements Instrument {

    private static Logger logger = LoggerFactory.getLogger(BasicInstrument.class);

    protected List<Zone> zones;
    private String name;
    private Integer fixedRoot;
    private Boolean looping;

    private ZoneFactory zoneFactory;
    private SampleFactory sampleFactory;
    private FolderOfSamples sourceFolderOfSamples;

    public BasicInstrument(String name) {
        this.name = name;
        zones = new ArrayList<Zone>();
        looping = false;
    }

    void addZone(Zone zone) {
        zones.add(zone);
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSamples(List<Sample> samples) {
        int lowkey = 0;

        for (Sample sample : samples) {
            Zone zone = zoneFactory.createZone();
            zone.setSample(sample);
            zone.setKeyRange(lowkey, lowkey);

            if (fixedRoot != null) {
                sample.setRoot(fixedRoot);
            } else {
                sample.setRoot(lowkey);
            }

            lowkey++;

            addZone(zone);
        }
    }

    public void setFixedRoot(Integer root) {
        fixedRoot = root;
    }

    public void loopAll(Boolean loop) {
        looping = loop;
    }


    public ZoneFactory getZoneFactory() {
        return zoneFactory;
    }

    public void setZoneFactory(ZoneFactory zoneFactory) {
        this.zoneFactory = zoneFactory;
    }

    @Override
    public File getSamplesDirectory() {
        return sourceFolderOfSamples.getDirectory();
    }

    public File getSourceDirectory() {
        return sourceFolderOfSamples.getSourceDirectory();
    }

    @Override
    public void setSource(FolderOfSamples fos) {
        sourceFolderOfSamples = fos;
    }

    @Override
    public FolderOfSamples getSource() {
        return sourceFolderOfSamples;
    }

    public SampleFactory getSampleFactory() {
        return sampleFactory;
    }

    public void setSampleFactory(SampleFactory sampleFactory) {
        this.sampleFactory = sampleFactory;
    }

    public Boolean getLooping() {
        return looping;
    }

    public void setLooping(Boolean looping) {
        this.looping = looping;
    }

}
