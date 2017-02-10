package com.spriton.therapypi.components;


import org.apache.log4j.Logger;

public class Sound {

    private static Logger log = Logger.getLogger(Sound.class);

    public static void playTimerAlarm() {
        try {
            log.info("Playing Timer Alarm Sound");



        } catch(Exception ex) {
            log.error("Error playing sound", ex);
        }
    }

}
