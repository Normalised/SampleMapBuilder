package com.relivethefuture.kitbuilder.output;

import com.relivethefuture.formats.SamplerFormat;
import com.relivethefuture.generic.*;
import com.relivethefuture.generic.InstrumentFactory;
import com.relivethefuture.kitbuilder.writers.InstrumentWriter;

/**
 */
public class OutputConfig extends GenericOutput {

    private SamplerFormat format;
    private InstrumentWriter writer;
    private InstrumentFactory instrumentFactory;

    private SampleFactory sampleFactory;
    private ZoneFactory zoneFactory;

    public OutputConfig() {
        instrumentFactory = new BasicInstrumentFactory();
        sampleFactory = new BasicSampleFactory();
        zoneFactory = new BasicZoneFactory();
    }

    public InstrumentWriter getWriter() {
        return writer;
    }

    public void setWriter(InstrumentWriter writer) {
        this.writer = writer;
    }

    public SamplerFormat getFormat() {
        return format;
    }

    public void setFormat(SamplerFormat format) {
        this.format = format;
        if(getRelativePath() == null) {
            setRelativePath(format.type());
        }
    }

    public InstrumentFactory getInstrumentFactory() {
        return instrumentFactory;
    }

    public void setInstrumentFactory(InstrumentFactory instrumentFactory) {
        this.instrumentFactory = instrumentFactory;
    }

    public SampleFactory getSampleFactory() {
        return sampleFactory;
    }

    protected void setSampleFactory(SampleFactory sampleFactory) {
        this.sampleFactory = sampleFactory;
    }

    public ZoneFactory getZoneFactory() {
        return zoneFactory;
    }

    protected void setZoneFactory(ZoneFactory zoneFactory) {
        this.zoneFactory = zoneFactory;
    }
}
