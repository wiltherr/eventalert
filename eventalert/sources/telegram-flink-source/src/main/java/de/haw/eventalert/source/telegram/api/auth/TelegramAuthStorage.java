package de.haw.eventalert.source.telegram.api.auth;

import com.github.badoualy.telegram.api.TelegramApiStorage;
import com.github.badoualy.telegram.mtproto.auth.AuthKey;
import com.github.badoualy.telegram.mtproto.model.DataCenter;
import com.github.badoualy.telegram.mtproto.model.MTSession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Tim on 06.09.2017.
 */
public class TelegramAuthStorage implements TelegramApiStorage {

    private byte[] authKeyBytes;
    private DataCenter dataCenter;

    public TelegramAuthStorage(TelegramAuthentication telegramAuthentication) {
        this.authKeyBytes = telegramAuthentication.getAuthKey();
        this.dataCenter = telegramAuthentication.getDataCenter();
    }

    TelegramAuthStorage() {
        //authKey and dataCenter will be set by kotlogram while authentication via saveAuthKey() and saveDc()
        this.authKeyBytes = null;
        this.dataCenter = null;
    }

    public TelegramAuthentication export() {
        return new TelegramAuthentication(authKeyBytes, dataCenter);
    }

    public DataCenter getDataCenter() {
        return dataCenter;
    }

    private boolean hasAuthKey() {
        return this.authKeyBytes != null;
    }

    @Override
    public void saveAuthKey(@NotNull AuthKey authKey) {
        this.authKeyBytes = authKey.getKey();
    }

    @Nullable
    @Override
    public AuthKey loadAuthKey() {
        if (hasAuthKey())
            return new AuthKey(this.authKeyBytes);
        else
            return null;
    }

    @Override
    public void saveDc(@NotNull DataCenter dataCenter) {
        this.dataCenter = dataCenter;
    }

    @Nullable
    @Override
    public DataCenter loadDc() {
        return this.dataCenter;
    }

    @Override
    public void deleteAuthKey() {
        this.authKeyBytes = null;
    }

    @Override
    public void deleteDc() {
        this.dataCenter = null;
    }

    @Override
    public void saveSession(@Nullable MTSession session) {

    }

    @Nullable
    @Override
    public MTSession loadSession() {
        return null;
    }
}
