package com.spriton.therapypi;

import com.spriton.therapypi.components.*;
import com.spriton.therapypi.components.hardware.*;
import com.spriton.therapypi.database.DataAccess;
import org.apache.log4j.*;

public class MainController {

    private static Logger log = Logger.getLogger(MainController.class);

    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure("log4j.properties");
            Config.init(args, "therapypi.properties");

            if(Config.values.getBoolean("SET_VOLUME_ON_BOOT", true)) {
                try {
                    log.info("Setting PCM volume to 100%");
                    Runtime runtime = Runtime.getRuntime();
                    runtime.exec("amixer sset 'PCM' 100%");
                } catch(Exception ex) {
                    log.error("Unable to set volume on boot", ex);
                }
            }

            DataServer.init();
            DataAccess.init();

            if (Config.values.getBoolean("HARDWARE_MACHINE", false)) {
                log.info("Setting up hardware machine");
                //SpiInterface.init();
                Machine.setInstance(Machine.Type.HARDWARE);
            } else {
                log.info("Setting up software machine");
                Machine.setInstance(Machine.Type.SOFTWARE);
            }

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    log.info("Shutting down...");
                    DataAccess.shutdown();
                    Machine.instance().shutdown();
                }
            });

            log.info("Running machine...");
            if (Config.values.getBoolean("OPTICAL_ENCODER", true)) {
                Machine.instance().startEventHandling();
            } else {
                Machine.instance().run();
            }

        } catch(Exception ex) {
            log.error("Unable to start. ", ex);
        }
    }



}
