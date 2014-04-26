package com.relivethefuture.formats.ableton;

/**
 * Created by martin on 21/01/12 at 20:35
 */
public class AbletonDrumRackSimplersWriter extends AbletonWriter {

    public AbletonDrumRackSimplersWriter() {
        itemTemplate = "ableton/sections/SimplerRackItemTemplate.xml";
        mainTemplate = "ableton/DrumRackTemplate.xml";
        setupTemplates();
    }
}
