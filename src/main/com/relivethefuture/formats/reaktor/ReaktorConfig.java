package com.relivethefuture.formats.reaktor;

import com.relivethefuture.formats.SamplerFormat;
import com.relivethefuture.kitbuilder.output.OutputConfig;

/**
 * Created by martin on 08/01/12 at 16:56
 */
public class ReaktorConfig extends OutputConfig {

    public ReaktorConfig() {
        super();
        setWriter(new ReaktorSampleMapWriter());
        setFormat(SamplerFormat.REAKTOR);
    }
}
