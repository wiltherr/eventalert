package de.haw.eventalert.source.telegram.api.auth;

import com.github.badoualy.telegram.mtproto.model.DataCenter;
import lombok.Getter;

@Getter
public class TelegramAuthentication {
    private byte[] authKey;
    private DataCenter dataCenter;

    public TelegramAuthentication(byte[] authKeyBytes, DataCenter dataCenter) {
        this.authKey = authKeyBytes;
        this.dataCenter = dataCenter;
    }
}
