package com.spriton.therapypi;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.log4j.Logger;

import java.io.File;

public class Config  {

    private static Logger log = Logger.getLogger(Config.class);
    private static Configurations configs = new Configurations();
    public static Configuration config = null;

    public static void init(String propertiesFile) throws Exception {
        File file = new File(propertiesFile);
        log.info("Loading properties: " + file.getAbsolutePath());
        config = configs.properties(file);
    }

}
