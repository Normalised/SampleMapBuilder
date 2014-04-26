package com.relivethefuture.generic;

/**
 * Created by martin on 08/01/12 at 15:58
 */
public class BasicInstrumentFactory implements InstrumentFactory {
    public Instrument createInstrument(String name) {
        return new BasicInstrument(name);
    }
}
