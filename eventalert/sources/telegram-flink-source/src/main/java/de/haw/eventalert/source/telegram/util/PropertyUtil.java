package de.haw.eventalert.source.telegram.util;

import java.io.IOException;
import java.util.Properties;

public class PropertyUtil {
    /**
     * file name of telegram api configuration
     */
    public static final String TELEGRAM_API_PROPERTY_FILE_PATH = "telegram-api.properties";

    private static Properties apiProperties;

    public static Properties getApiProperties() throws IOException {
        if (apiProperties == null) {
            apiProperties = new Properties();
            apiProperties.load(getClassLoader().getResourceAsStream(TELEGRAM_API_PROPERTY_FILE_PATH));
        }
        return apiProperties;
    }

    private static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
