package de.haw.eventalert.source.telegram.client.util;

import com.github.badoualy.telegram.api.TelegramClient;
import com.github.badoualy.telegram.tl.api.*;
import com.github.badoualy.telegram.tl.exception.RpcErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Tim on 28.09.2017.
 */
public class User {

    private static final Logger LOG = LoggerFactory.getLogger(User.class);

    public static String getName(TelegramClient telegramClient, int userId) {
        TLUserFull user = getUser(telegramClient, userId);
        return extractName(user);
    }

    public static String getSelfName(TelegramClient telegramClient) {
        TLUserFull selfUser = getSelfUser(telegramClient);
        if (selfUser != null)
            return extractName(selfUser);
        return null;
    }

    public static Integer getSelfId(TelegramClient telegramClient) {
        TLUserFull selfUser = getSelfUser(telegramClient);
        return extractId(selfUser);
    }

    public static TLUserFull getUser(TelegramClient telegramClient, int userId) {
        try {
            TLInputUser user = new TLInputUser();
            user.setUserId(userId);
            return telegramClient.usersGetFullUser(user);
        } catch (RpcErrorException e) {
            LOG.error("error getting user for id {}", userId, e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TLUserFull getSelfUser(TelegramClient telegramClient) {
        try {
            return telegramClient.usersGetFullUser(new TLInputUserSelf());
        } catch (RpcErrorException e) {
            LOG.error("can not find self user");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String extractName(TLUserFull tlUserFull) {
        if (tlUserFull.getUser().isEmpty())
            return null;
        return extractName(tlUserFull.getUser().getAsUser());
    }

    public static String extractName(TLUser tlUser) {
        StringBuilder userName = new StringBuilder();
        if (tlUser.getFirstName() != null) {
            userName.append(tlUser.getFirstName());
        }
        if (tlUser.getLastName() != null) {
            userName.append(" ").append(tlUser.getLastName());
        }
        return userName.toString();
    }

    public static Integer extractId(TLUserFull tlUserFull) {
        if (tlUserFull.getUser().isEmpty())
            return null;

        return extractId(tlUserFull.getUser());
    }

    public static Integer extractId(TLAbsUser tlAbsUser) {
        return tlAbsUser.getId();
    }
}
