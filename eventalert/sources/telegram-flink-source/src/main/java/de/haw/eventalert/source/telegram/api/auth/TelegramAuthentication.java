package de.haw.eventalert.source.telegram.api.auth;

import com.github.badoualy.telegram.mtproto.model.DataCenter;

public class TelegramAuthentication {
    private byte[] authKey;
    private DataCenter dataCenter;

    public TelegramAuthentication(byte[] authKeyBytes, DataCenter dataCenter) {
        this.authKey = authKeyBytes;
        this.dataCenter = dataCenter;
    }

    public byte[] getAuthKey() {
        return authKey;
    }

    public DataCenter getDataCenter() {
        return dataCenter;
    }
}
