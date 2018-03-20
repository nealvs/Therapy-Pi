package com.spriton.therapypi.components;

import com.spriton.therapypi.database.ConfigValue;
import com.spriton.therapypi.database.DataAccess;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.*;

public class Sound {

    private static Logger log = Logger.getLogger(Sound.class);
    private static Integer volume;

    public static int getVolume() {
        if(volume == null) {
            init();
        }
        return volume.intValue();
    }

    public static void setVolume(int newVolume) {
        volume = newVolume;
    }

    // Need to max out the Pi's volume
    // amixer scontrols
    // amixer sset 'Master' 50%

    public static void init() {
        ConfigValue volumeConfig = DataAccess.getConfigValue("VOLUME");
        if(volumeConfig != null) {
            volume = Integer.parseInt(volumeConfig.getConfigValue());
        } else {
            volume = 100;
        }
    }

    public static void playTimerAlarm() {
        try {
            init();
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
            playSound();
        }

        private static synchronized void playSound() {
            try(InputStream inputStream = new BufferedInputStream(
                    SoundThread.class.getClassLoader().getResourceAsStream("beep.wav"));
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(inputStream)) {
                AudioFormat format = audioIn.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                Clip clip = (Clip) AudioSystem.getLine(info);
                if(!clip.isRunning()) {
                    clip.open(audioIn);

                    FloatControl masterGain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    float range = masterGain.getMaximum() - masterGain.getMinimum();
                    float scaledRange = range * volume / 100; // Scale the range 0-100%
                    float value = masterGain.getMinimum() + scaledRange;
                    log.debug("Sound MasterGain=" + value);
                    masterGain.setValue(value);

                    clip.start();
                    Thread.sleep(clip.getMicrosecondLength() / 1000);
                } else {
                    log.info("Not playing sound, clip is running already");
                }
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException  e1) {
                log.error("Error playing sound.", e1);
            }
        }
    }
}
