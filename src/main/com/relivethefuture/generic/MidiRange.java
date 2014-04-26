package com.relivethefuture.generic;

/**
 * Created by martin on 24/04/11 at 21:02
 */
public class MidiRange {
    private Integer low;
    private Integer high;

    public MidiRange() {
        low = 0;
        high = 127;
    }

    public MidiRange(Integer lowKey, Integer highKey) {
        low = lowKey;
        high = highKey;
    }

    public Integer getLow() {
        return low;
    }

    public void setLow(Integer low) {
        this.low = low;
    }

    public Integer getHigh() {
        return high;
    }

    public void setHigh(Integer high) {
        this.high = high;
    }

    public void setRange(Integer low,Integer high) {
        if(low <= high) {
            this.low = low;
            this.high = high;
        } else {
            this.low = high;
            this.high = low;
        }
    }
}
