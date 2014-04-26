package com.relivethefuture.generic;

/**
 * Created by martin on 25/04/11 at 10:53
 */
public class BasicZone implements Zone {
    
    private MidiRange velocityRange;
    private MidiRange noteRange;
    private Integer baseNote = 60;
    protected Sample sample;

    public BasicZone() {
        defaults();
    }
    
    public MidiRange getNoteRange() {
        return noteRange;
    }

    public MidiRange getVelocityRange() {
        return velocityRange;
    }

    public void setKeyRange(Integer lowKey, Integer highKey) {
        noteRange.setRange(lowKey,highKey);
    }

    public void setSample(Sample s) {
        sample = s;
    }

    public Sample getSample() {
        return sample;
    }

    void defaults() {
        noteRange = new MidiRange();
        velocityRange = new MidiRange();
        velocityRange.setLow(1);
        baseNote = 60;
    }

    public Integer getBaseNote() {
        return baseNote;
    }

    public void setBaseNote(Integer baseNote) {
        this.baseNote = baseNote;
    }
}
