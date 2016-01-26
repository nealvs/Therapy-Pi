package com.spriton.therapypi;


import org.apache.log4j.Logger;

public class MainController {

    private static Logger log = Logger.getLogger(MainController.class);

    public static void main(String[] args) throws Exception {
        String propertiesFile = "therapypi.properties";
        if(args.length > 0) {
            propertiesFile = args[0];
        }
        Config.init(propertiesFile);


        log.info(Config.config.getString("test"));
    }

}
