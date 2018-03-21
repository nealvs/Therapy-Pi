package com.spriton.therapypi.components;

import com.spriton.therapypi.database.ConfigValue;
import com.spriton.therapypi.database.DataAccess;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

        private static Lock lock = new ReentrantLock();
        private static Clip lastClip;
        private static long microSecondLength;

        @Override
        public void run() {
            playSound();
        }

        private static void playSound() {

            try {
                try (InputStream inputStream = new BufferedInputStream(
                        SoundThread.class.getClassLoader().getResourceAsStream("beep.wav"));
                     AudioInputStream audioIn = AudioSystem.getAudioInputStream(inputStream)) {

                    try {
                        lock.lock();
                        if (lastClip == null) {
                            AudioFormat format = audioIn.getFormat();
                            DataLine.Info info = new DataLine.Info(Clip.class, format);
                            lastClip = (Clip) AudioSystem.getLine(info);
                        }

                        if(lastClip.isRunning()) {
                            log.debug("Stopping sound position=" + lastClip.getMicrosecondPosition() + " length=" + lastClip.getMicrosecondLength());
                            lastClip.stop();
                            // Found that if you don't let it stop before closing it, it will get hung up for a second
                            Thread.sleep(1);
                        }
                        lastClip.close();

                        lastClip.open(audioIn);
                        microSecondLength = lastClip.getMicrosecondLength();
                        // Set volume with latest value
                        FloatControl masterGain = (FloatControl) lastClip.getControl(FloatControl.Type.MASTER_GAIN);
                        float range = masterGain.getMaximum() - masterGain.getMinimum();
                        // Make anything under 50 be mute
                        int vol = volume <= 50 ? 0 : volume;
                        float scaledRange = range * vol / 100; // Scale the range 0-100%
                        float value = masterGain.getMinimum() + scaledRange;
                        log.debug("Sound MasterGain=" + value);
                        masterGain.setValue(value);

                        lastClip.start();
                    } finally {
                        lock.unlock();
                    }
                    //Thread.sleep(microSecondLength / 1000);
                }

            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e1) {
                log.error("Error playing sound.", e1);
            }

        }
    }
}
