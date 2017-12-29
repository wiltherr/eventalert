package de.haw.eventalert.source.telegram.api;

import com.github.badoualy.telegram.mtproto.model.DataCenter;

import java.io.Serializable;
import java.util.Properties;

/**
 * Created by Tim on 06.09.2017.
 */
public class ApiConfiguration implements Serializable {

    private static final String PROPERTY_KEY_API_ID = "telegram.api.id";
    private static final String PROPERTY_KEY_API_HASH = "telegram.api.hash";

    private static final String PROPERTY_KEY_DATA_CENTER_IP = "telegram.data.center.ip";
    private static final String PROPERTY_KEY_DATA_CENTER_PORT = "telegram.data.center.port";

    private static final String PROPERTY_KEY_APP_VERSION = "app.version";
    private static final String PROPERTY_KEY_APP_MODEL = "app.model";
    private static final String PROPERTY_KEY_APP_SYSTEM_VERSION = "app.system.version";
    private static final String PROPERTY_KEY_APP_LANG_CODE = "app.lang.code";

    private int apiId;
    private String apiHash;

    private String appVersion;
    private String appModel;
    private String appSystemVersion;
    private String appLangCode;

    private String dataCenterIp;
    private int dataCenterPort;

    private ApiConfiguration(Properties properties) {
        this.apiId = Integer.parseInt(properties.getProperty(PROPERTY_KEY_API_ID));
        this.apiHash = properties.getProperty(PROPERTY_KEY_API_HASH);
        this.appModel = properties.getProperty(PROPERTY_KEY_APP_MODEL);
        this.appVersion = properties.getProperty(PROPERTY_KEY_APP_VERSION);
        this.appSystemVersion = properties.getProperty(PROPERTY_KEY_APP_SYSTEM_VERSION);
        this.appLangCode = properties.getProperty(PROPERTY_KEY_APP_LANG_CODE);
        this.dataCenterIp = properties.getProperty(PROPERTY_KEY_DATA_CENTER_IP);
        this.dataCenterPort = Integer.parseInt(properties.getProperty(PROPERTY_KEY_DATA_CENTER_PORT));
    }

    public static ApiConfiguration fromProperties(Properties properties) {
        return new ApiConfiguration(properties);
    }

    public int getApiId() {
        return apiId;
    }

    public String getApiHash() {
        return apiHash;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getAppModel() {
        return appModel;
    }

    public String getAppSystemVersion() {
        return appSystemVersion;
    }

    public String getAppLangCode() {
        return appLangCode;
    }

    public DataCenter getDefaultDataCenter() {
        return new DataCenter(dataCenterIp, dataCenterPort);
    }
}
