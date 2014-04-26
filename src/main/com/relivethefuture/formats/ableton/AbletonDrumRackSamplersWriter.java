package com.relivethefuture.formats.ableton;

/**
 * Created by martin on 21/01/12 at 20:35
 */
public class AbletonDrumRackSamplersWriter extends AbletonWriter {

    public AbletonDrumRackSamplersWriter() {
        itemTemplate = "ableton/sections/SamplerRackItemTemplate.xml";
        mainTemplate = "ableton/DrumRackTemplate.xml";
        setupTemplates();
    }
}
