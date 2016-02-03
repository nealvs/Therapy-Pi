package com.spriton.therapypi;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.log4j.Logger;

import java.io.File;

public class Config  {

    private static Logger log = Logger.getLogger(Config.class);
    private static Configurations configs = new Configurations();
    public static Configuration values = null;

    public static void init(String[] args, String propertiesFile) throws Exception {
        if(args != null && args.length > 0) {
            propertiesFile = args[0];
        }
        File file = new File(propertiesFile);
        log.info("Loading properties: " + file.getAbsolutePath());
        values = configs.properties(file);

    }

}
