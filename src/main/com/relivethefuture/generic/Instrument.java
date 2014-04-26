package com.relivethefuture.generic;

import com.relivethefuture.kitbuilder.model.FolderOfSamples;

import java.io.File;
import java.util.List;

public interface Instrument {
    public void setName(String name);
    public String getName();

    public void setZones(List<Zone> zones);
    List<Zone> getZones();

    public void setSampleFactory(SampleFactory sampleFactory);
    public void setZoneFactory(ZoneFactory zoneFactory);

    public File getSamplesDirectory();
    public File getSourceDirectory();

    public void setSource(FolderOfSamples fos);
    public FolderOfSamples getSource();
}
