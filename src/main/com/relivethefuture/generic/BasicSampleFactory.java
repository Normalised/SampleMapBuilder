package com.relivethefuture.generic;

import java.io.File;

/**
 * Created by martin on 08/01/12 at 16:49
 */
public class BasicSampleFactory implements SampleFactory {
    public Sample createSample(File file) {
        return new BasicSample(file);
    }
}
