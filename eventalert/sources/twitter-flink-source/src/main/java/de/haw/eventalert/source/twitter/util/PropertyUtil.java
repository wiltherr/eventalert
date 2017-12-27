package de.haw.eventalert.source.twitter.util;

import java.io.IOException;
import java.util.Properties;

public class PropertyUtil {
    /**
     * file name of telegram api configuration
     */
    public static final String TWITTER_API_PROPERTY_FILE_PATH = "twitter-api.properties";

    private static Properties apiProperties;

    public static Properties getApiProperties() {
        if (apiProperties == null) {
            apiProperties = new Properties();
            try {
                apiProperties.load(getClassLoader().getResourceAsStream(TWITTER_API_PROPERTY_FILE_PATH));
            } catch (IOException e) {
                throw new IllegalArgumentException("could not find or load '" + TWITTER_API_PROPERTY_FILE_PATH + "' file");
            }
        }
        return apiProperties;
    }

    private static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
