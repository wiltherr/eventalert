package de.haw.eventalert.source.email.configuration;

import java.io.Serializable;
import java.util.Properties;

/**
 * value object for email source configuration
 * can be created with {@link EMailSourceConfiguration#fromProperties(Properties)}
 */
public class EMailSourceConfiguration implements Serializable { //TODO in der grafik das paket ändern?

    public static final String PROPERTY_KEY_SOURCE_ID = "source.id";
    public static final String PROPERTY_KEY_EMAIL_SERVER_IP = "email.server.ip";
    public static final String PROPERTY_KEY_EMAIL_SERVER_PORT = "email.server.port";
    public static final String PROPERTY_KEY_EMAIL_SERVER_SECURE = "email.server.secure";
    public static final String PROPERTY_KEY_EMAIL_LOGIN_USER = "email.login.user";
    public static final String PROPERTY_KEY_EMAIL_LOGIN_PASSWORD = "email.login.password";
    public static final String PROPERTY_KEY_EMAIL_FOLDER = "email.folder.name";

    private long id;

    private String host;
    private int port;
    private boolean secure;

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
        EMailSourceConfiguration conf = new EMailSourceConfiguration();
        conf.setId(Long.valueOf(properties.getProperty(PROPERTY_KEY_SOURCE_ID)))
                .setHost(properties.getProperty(PROPERTY_KEY_EMAIL_SERVER_IP))
                .setPort(Integer.parseInt(properties.getProperty(PROPERTY_KEY_EMAIL_SERVER_PORT)))
                .setSecure(Boolean.parseBoolean(properties.getProperty(PROPERTY_KEY_EMAIL_SERVER_SECURE)))
                .setUser(properties.getProperty(PROPERTY_KEY_EMAIL_LOGIN_USER))
                .setPassword(properties.getProperty(PROPERTY_KEY_EMAIL_LOGIN_PASSWORD))
                .setFolder(properties.getProperty(PROPERTY_KEY_EMAIL_FOLDER));

        return conf;
    }

    public long getId() {
        return id;
    }

    public EMailSourceConfiguration setId(long id) {
        this.id = id;
        return this;
    }

    public String getHost() {
        return host;
    }

    public EMailSourceConfiguration setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public EMailSourceConfiguration setPort(int port) {
        this.port = port;
        return this;
    }

    public boolean isSecure() {
        return secure;
    }

    public EMailSourceConfiguration setSecure(boolean secure) {
        this.secure = secure;
        return this;
    }

    public String getUser() {
        return user;
    }

    public EMailSourceConfiguration setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public EMailSourceConfiguration setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getFolder() {
        return folder;
    }

    public EMailSourceConfiguration setFolder(String folder) {
        this.folder = folder;
        return this;
    }
}
