package de.haw.eventalert.source.twitter;

import java.io.IOException;
import java.util.Properties;

public class ApiProperties {
    public static final Properties apiProperties;
    /**
     * file name of telegram api configuration
     */
    private static final String TWITTER_API_PROPERTY_FILE_PATH = "twitter-api.properties";

    static {
        apiProperties = new Properties();
        try {
            apiProperties.load(getClassLoader().getResourceAsStream(TWITTER_API_PROPERTY_FILE_PATH));
        } catch (IOException e) {
            throw new IllegalArgumentException("could not find or load '" + TWITTER_API_PROPERTY_FILE_PATH + "' file");
        }
    }

    private static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
