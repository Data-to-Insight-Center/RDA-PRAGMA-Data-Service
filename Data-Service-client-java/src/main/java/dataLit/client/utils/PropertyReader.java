package dataLit.client.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {

    private static PropertyReader instance = null;
    private static Properties properties;

    private PropertyReader(String propertiesPath) {
        try {
            FileInputStream fileInputStream = new FileInputStream(propertiesPath);
            properties = new Properties();
            properties.load(fileInputStream);
        } catch (IOException e) {
            System.err.println("ERROR: Unable to load properties file " + propertiesPath);
            e.printStackTrace(System.err);
            System.exit(-1);
        }
    }

    public static PropertyReader getInstance(String propertiesPath) {
        if (instance == null) {
            instance = new PropertyReader(propertiesPath);
        }
        return instance;
    }

    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

}