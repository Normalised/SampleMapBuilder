package com.relivethefuture.kitbuilder.writers;

import com.relivethefuture.generic.Instrument;
import com.relivethefuture.kitbuilder.SimpleProgressListener;

import java.io.File;

public interface InstrumentWriter {
    void write(Instrument instrument, File outputDirectory);
}
