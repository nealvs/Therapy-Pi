package com.spriton.therapypi.components;

import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.*;

public class Sound {

    private static Logger log = Logger.getLogger(Sound.class);

    public static void playTimerAlarm() {
        try {
            log.info("Playing Timer Alarm Sound");
            Thread t = new Thread(new SoundThread());
            t.start();
        } catch(Exception ex) {
            log.error("Error playing sound", ex);
        }
    }

    public static class SoundThread implements Runnable {
        @Override
        public void run() {
            try(InputStream inputStream = new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("beep.wav"));
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(inputStream)) {
                AudioFormat format = audioIn.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                Clip clip = (Clip) AudioSystem.getLine(info);
                clip.open(audioIn);
                clip.start();
                Thread.sleep(clip.getMicrosecondLength() / 1000);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException  e1) {
                log.error("Error playing sound.", e1);
            }
        }
    }
}
