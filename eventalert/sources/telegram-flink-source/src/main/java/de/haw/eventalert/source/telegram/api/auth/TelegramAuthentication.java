package de.haw.eventalert.source.telegram.api.auth;

import com.github.badoualy.telegram.mtproto.model.DataCenter;

import java.io.Serializable;

public class TelegramAuthentication implements Serializable {
    private byte[] authKey;
    private String dataCenterIp;
    private int dataCenterPort;

    public TelegramAuthentication(byte[] authKeyBytes, String dataCenterIp, int dataCenterPort) {
        this.authKey = authKeyBytes;
        this.dataCenterIp = dataCenterIp;
        this.dataCenterPort = dataCenterPort;
    }

    public byte[] getAuthKey() {
        return authKey;
    }

    public DataCenter getDataCenter() {
        return new DataCenter(dataCenterIp, dataCenterPort);
    }
}
