package com.relivethefuture.generic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.util.zip.CRC32;

/**
 * Created by martin on 25/04/11 at 10:59
 */
public class BasicSample implements Sample, Comparable<Sample> {

    private static Logger logger = LoggerFactory.getLogger(BasicSample.class);

    private LoopSettings loopSettings;
    private Integer root;
    private File file;
    private Float tune;
    private Float volume;
    private Float pan;
    private Integer duration;
    private Long crc;
    private boolean excluded;

    protected BasicSample() {
        defaults();
    }

    public BasicSample(File f) {
        file = f;
        defaults();
    }

    /**
     * Copy Constructor
     * Note : this doesnt
     * @param source    Sample to copy
     */
    public BasicSample(Sample source) {
        file = new File(source.getFile().getAbsolutePath());
        root = source.getRoot();
        tune = source.getTune();
        volume = source.getVolume();
        pan = source.getPan();
        if(source instanceof BasicSample) {
            // Just copy duration, the getDuration accessor will trigger the file read / analysis
            // if the duration hasn't yet been calculated.
            BasicSample bs = (BasicSample) source;
            duration = bs.duration;
        }
        excluded = source.getExclude();
        loopSettings = new LoopSettings(source.getLoopSettings());
    }

    void defaults() {
        tune = 0.0f;
        volume = 1.0f;
        pan = 0.5f;
        root = 48;
        loopSettings = new LoopSettings();
    }

    public Float getTune() {
        return tune;
    }

    public Float getVolume() {
        return volume;
    }

    public void setDuration(Integer d) {
        duration = d;
    }

    public Integer getDuration() throws UnsupportedAudioFileException, IOException {
        if(duration == null) {
            try {
                AudioFileFormat aff = AudioSystem.getAudioFileFormat(file);
                duration = aff.getFrameLength();
                logger.debug("Got duration " + duration);
            } catch (Exception e) {
                logger.warn("Error getting duration for " + file.getAbsolutePath());
            }
        }
        return duration;
    }

    /**
     * Was in the Ableton format but isn't required, so its never used.
     * Might be useful for other formats in the future
     * @return
     * @throws IOException
     */
    public Long getCrc() throws IOException {
        if(crc == null) { 
            InputStream in = new FileInputStream(file);
            CRC32 crc32 = new CRC32();

            int c;
            while ((c = in.read()) != -1) {
                crc32.update(c);
                crc = crc32.getValue();
            }
        }
        return crc;
    }

    public boolean getExclude() {
        return excluded;
    }

    public void setExclude(boolean e) {
        excluded = e;
    }

    public Float getPan() {
        return pan;
    }

    public LoopSettings getLoopSettings() {
        return loopSettings;
    }

    public void setLoopSettings(LoopSettings loopSettings) {
        this.loopSettings = loopSettings;
    }

    public Integer getRoot() {
        return root;
    }

    public void setRoot(Integer root) {
        this.root = root;
    }

    public Boolean getLooping() {
        return loopSettings.isLooped();
    }

    public void setLooping(Boolean b) {
        loopSettings.setLooped(b);
    }

    public void setTune(Float t) {
        tune = t;
    }

    public void setPan(Float p) {
        pan = p;
    }

    public void setVolume(Float v) {
        volume = v;
    }

    public String getName() {
        return file.getName();
    }

    public long getSize() {
        return file.length();
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Sample clone() {
        Sample s = new BasicSample(file);
        s.setVolume(volume);
        s.setPan(pan);
        s.setLoopSettings(loopSettings.clone());
        s.setTune(tune);
        if(duration != null) {
            s.setDuration(duration);
        }
        return s;
    }

    public int compareTo(Sample sample) {
        return (int) (getSize() - sample.getSize());
    }
}
