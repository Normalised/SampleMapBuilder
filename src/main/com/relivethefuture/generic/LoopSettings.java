package com.relivethefuture.generic;

/**
 * Created by martin on 24/04/11 at 21:06
 */
public class LoopSettings implements Cloneable {
    private Boolean looped = false;
    private Integer loopStart;
    private Integer loopEnd;

    public LoopSettings() {
        loopStart = 0;
        loopEnd = 0;
    }

    public LoopSettings(LoopSettings source) {
        looped = source.looped;
        loopStart = source.loopStart;
        loopEnd = source.loopEnd;
    }

    public void setLoopPoints(Integer start,Integer end) {
        if(start <= end) {
            loopStart = start;
            loopEnd = end;
        } else {
            loopStart = end;
            loopEnd = start;
        }
    }

    public Integer getLoopStart() {
        return loopStart;
    }

    public Integer getLoopEnd() {
        return loopEnd;
    }

    public void setLooped(Boolean l){
        looped = l;
    }
    
    public Boolean isLooped() {
        return looped;
    }

    public void setLoopStart(Integer loopStart) {
        this.loopStart = loopStart;
    }

    public void setLoopEnd(int loopEnd) {
        this.loopEnd = loopEnd;
    }
    
    public LoopSettings clone() {
        LoopSettings l = new LoopSettings();
        l.setLooped(looped);
        l.setLoopPoints(loopStart,loopEnd);
        return l;
    }
}
