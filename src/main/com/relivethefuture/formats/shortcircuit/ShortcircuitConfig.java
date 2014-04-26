package com.relivethefuture.formats.shortcircuit;

import com.relivethefuture.formats.SamplerFormat;
import com.relivethefuture.kitbuilder.output.OutputConfig;

/**
 * Created by martin on 08/01/12 at 17:55
 */
public class ShortcircuitConfig extends OutputConfig {
    public ShortcircuitConfig() {
        setWriter(new ShortcircuitWriter());
        setFormat(SamplerFormat.SHORTCIRCUIT);
    }
}
