package com.relivethefuture.generic;

/**
 * Created by martin on 24/04/11 at 21:22
 */
public interface Zone {

    MidiRange getNoteRange();
    MidiRange getVelocityRange();
    void setKeyRange(Integer lowKey, Integer highKey);
    void setSample(Sample s);
    Sample getSample();
    
    void setBaseNote(Integer baseNote);
    Integer getBaseNote();
}
