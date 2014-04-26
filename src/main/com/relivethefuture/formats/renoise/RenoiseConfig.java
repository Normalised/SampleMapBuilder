package com.relivethefuture.formats.renoise;

import com.relivethefuture.formats.SamplerFormat;
import com.relivethefuture.kitbuilder.output.OutputConfig;

/**
 * Created by martin on 08/01/12 at 17:55
 */
public class RenoiseConfig extends OutputConfig {
    public RenoiseConfig() {
        setWriter(new RenoiseWriter());
        setFormat(SamplerFormat.RENOISE_INSTRUMENT);
    }
}
