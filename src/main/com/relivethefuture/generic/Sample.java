package com.relivethefuture.generic;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Created by martin on 25/04/11 at 10:57
 */
public interface Sample {

    String getName();
    long getSize();

    File getFile();
    void setFile(File file);

    void setLoopSettings(LoopSettings loopSettings);
    LoopSettings getLoopSettings();

    Integer getRoot();
    void setRoot(Integer root);

    Boolean getLooping();
    void setLooping(Boolean b);

    void setTune(Float t);
    void setPan(Float p);
    void setVolume(Float v);
    
    Float getTune();
    Float getPan();
    Float getVolume();

    Integer getDuration() throws UnsupportedAudioFileException, IOException;
    void setDuration(Integer duration);

    Long getCrc() throws IOException;

    boolean getExclude();
    void setExclude(boolean e);
}
