package com.spriton.therapypi;

import com.spriton.therapypi.components.*;
import com.spriton.therapypi.components.hardware.*;
import com.spriton.therapypi.database.DataAccess;
import org.apache.log4j.*;

public class MainController {

    private static Logger log = Logger.getLogger(MainController.class);

    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log4j.properties");
        Config.init(args, "therapypi.properties");
        DataAccess.init();

        if(Config.values.getBoolean("hardware", false)) {
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

        //DataAccess.shutdown();
    }

}
