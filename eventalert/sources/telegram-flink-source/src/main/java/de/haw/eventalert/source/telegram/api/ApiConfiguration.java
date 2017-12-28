package de.haw.eventalert.source.telegram.api;

import com.github.badoualy.telegram.mtproto.model.DataCenter;
import lombok.Builder;
import lombok.Getter;

import java.util.Properties;

/**
 * Created by Tim on 06.09.2017.
 */
public @Getter
@Builder
class ApiConfiguration {

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

    private DataCenter defaultDataCenter;

    public static ApiConfiguration fromProperties(Properties properties) {
        ApiConfigurationBuilder builder = ApiConfiguration.builder();
        builder.apiId(Integer.parseInt(properties.getProperty(PROPERTY_KEY_API_ID)))
                .apiHash(properties.getProperty(PROPERTY_KEY_API_HASH))
                .appVersion(properties.getProperty(PROPERTY_KEY_APP_VERSION))
                .appModel(properties.getProperty(PROPERTY_KEY_APP_MODEL))
                .appSystemVersion(properties.getProperty(PROPERTY_KEY_APP_SYSTEM_VERSION))
                .appLangCode(properties.getProperty(PROPERTY_KEY_APP_LANG_CODE));

        DataCenter dataCenter = new DataCenter(properties.getProperty(PROPERTY_KEY_DATA_CENTER_IP), Integer.parseInt(properties.getProperty(PROPERTY_KEY_DATA_CENTER_PORT)));
        builder.defaultDataCenter(dataCenter);

        return builder.build();
    }
}
