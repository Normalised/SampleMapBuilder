package com.relivethefuture.formats.sfz;

import com.relivethefuture.formats.SamplerFormat;
import com.relivethefuture.kitbuilder.output.OutputConfig;

public class SfzConfig extends OutputConfig {

    public SfzConfig() {
        setWriter(new SFZWriter(this));
        setFormat(SamplerFormat.SFZ);
    }
}
