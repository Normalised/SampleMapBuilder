package com.relivethefuture.formats.ableton;

/**
 * Created by martin on 24/04/11 at 14:19
 */
public class AbletonSamplerZonesWriter extends AbletonWriter {

    public AbletonSamplerZonesWriter() {
        mainTemplate = "ableton/SamplerTemplate.xml";
        setupTemplates();
    }
}
