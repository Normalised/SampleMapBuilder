package com.relivethefuture.audio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by martin on 11/01/12 at 11:46
 */
public class AudioFilePlayer {

    private final Logger logger = LoggerFactory.getLogger(AudioFilePlayer.class);

    private Clip currentClip;

    private List<Clip> clips;

    private int currentClipIndex = 0;
    
    public AudioFilePlayer() {
        clips = new ArrayList<Clip>(8);
        try {
            for (int i = 0; i < 8; i++) {
                Clip clip = AudioSystem.getClip();
                clips.add(clip);
            }
        } catch (LineUnavailableException e) {
            logger.error("Cant create audio clip",e);
        }

        updateCurrentClip();

    }

    private void updateCurrentClip() {
        currentClip = clips.get(currentClipIndex++);
        if(currentClipIndex == 8) {
            currentClipIndex = 0;
        }
    }

    public void play(File file) {
        try {
            currentClip.close();
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat	sourceFormat = audioInputStream.getFormat();
            DataLine.Info	info = new DataLine.Info(SourceDataLine.class,
                    sourceFormat, AudioSystem.NOT_SPECIFIED);
            boolean	bIsSupportedDirectly = AudioSystem.isLineSupported(info);
            if (!bIsSupportedDirectly) {
                AudioFormat	targetFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        sourceFormat.getSampleRate(),
                        16,
                        sourceFormat.getChannels(),
                        sourceFormat.getChannels() * 2,
                        sourceFormat.getSampleRate(),
                        true);
                audioInputStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream);
            }

            currentClip.open(audioInputStream);
            currentClip.setFramePosition(0);
            currentClip.start();
            updateCurrentClip();
        } catch (LineUnavailableException e) {
            logger.error("Line Unavailable",e);
        } catch (UnsupportedAudioFileException e) {
            logger.error("Unsupported Audio File", e);
        } catch (IOException e) {
            logger.error("IO Exception",e);
        } catch (IllegalStateException e) {
            logger.error("Illegal State",e);
        }

    }
}
