package de.haw.eventalert.source.email.configuration;

import lombok.Builder;
import lombok.Getter;

import java.util.Properties;

/**
 * value object for email source configuration
 * can be created with {@link EMailSourceConfiguration#fromProperties(Properties)}
 */
public @Getter
@Builder
class EMailSourceConfiguration { //TODO in der grafik das paket ändern?

    private static final String PROPERTY_KEY_SOURCE_ID = "source.id";
    private static final String PROPERTY_KEY_EMAIL_SERVER_IP = "email.server.ip";
    private static final String PROPERTY_KEY_EMAIL_SERVER_PORT = "email.server.port";
    private static final String PROPERTY_KEY_EMAIL_LOGIN_USER = "email.login.user";
    private static final String PROPERTY_KEY_EMAIL_LOGIN_PASSWORD = "email.login.password";
    private static final String PROPERTY_KEY_EMAIL_FOLDER = "email.folder.name";

    private long id;

    private String host;
    private int port;

    private String user;
    private String password;

    private String folder;

    /**
     * generates {@link EMailSourceConfiguration} from properties
     *
     * @param properties properties with requested keys (an example property file exists in resource folder)
     * @return email source configuration
     */
    public static EMailSourceConfiguration fromProperties(Properties properties) {
        EMailSourceConfigurationBuilder builder = EMailSourceConfiguration.builder();
        builder.id(Long.valueOf(properties.getProperty(PROPERTY_KEY_SOURCE_ID)))
                .host(properties.getProperty(PROPERTY_KEY_EMAIL_SERVER_IP))
                .port(Integer.parseInt(properties.getProperty(PROPERTY_KEY_EMAIL_SERVER_PORT)))
                .user(properties.getProperty(PROPERTY_KEY_EMAIL_LOGIN_USER))
                .password(properties.getProperty(PROPERTY_KEY_EMAIL_LOGIN_PASSWORD))
                .folder(properties.getProperty(PROPERTY_KEY_EMAIL_FOLDER));
        return builder.build();
    }
}
