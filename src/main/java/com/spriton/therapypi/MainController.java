package com.spriton.therapypi;

import com.spriton.therapypi.components.*;
import com.spriton.therapypi.components.hardware.*;
import com.spriton.therapypi.components.software.*;
import org.apache.log4j.Logger;

public class MainController {

    private static Logger log = Logger.getLogger(MainController.class);

    public static void main(String[] args) throws Exception {
        Config.init(args, "therapypi.properties");

        if(Config.config.getBoolean("hardware", false)) {
            log.info("Setting up hardware machine");
            SpiInterface.init();
            Machine.setInstance(Machine.Type.HARDWARE);
        } else {
            log.info("Setting up software machine");
            Machine.setInstance(Machine.Type.SOFTWARE);
        }

        log.info("Running machine...");
        Machine.instance().run();

        DataServer.init();
    }

}
