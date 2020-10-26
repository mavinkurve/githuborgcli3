package com.example.githuborgcli.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyManager {
    private static Logger log = LogManager.getLogger();

    private static String fileName = "config.properties";

    private static Properties properties;

    private static PropertyManager ourInstance;

    static {
        try {
            ourInstance = new PropertyManager();
        } catch (IOException e) {
            log.fatal("Could not load system properties", e);
        }
    }

    private PropertyManager() throws IOException {
        try (InputStream input = PropertyManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            properties = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            //load a properties file from class path, inside static method
            properties.load(input);
        }
    }

    public static PropertyManager getInstance() {
        return ourInstance;
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    static Integer getAsInteger(String key) {
        return Integer.valueOf(properties.getProperty(key));
    }
}
