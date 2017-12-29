package de.haw.eventalert.source.telegram.client.util;

import com.github.badoualy.telegram.api.TelegramClient;
import com.github.badoualy.telegram.tl.api.*;
import com.github.badoualy.telegram.tl.core.TLIntVector;
import com.github.badoualy.telegram.tl.core.TLVector;
import com.github.badoualy.telegram.tl.exception.RpcErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Tim on 28.09.2017.
 */
public class Chat {

    private static final Logger LOG = LoggerFactory.getLogger(Chat.class);

    public static String getTitle(TelegramClient telegramClient, int chatId) {
        TLAbsChat tlAbsChat = getChat(telegramClient, chatId);
        if (tlAbsChat != null) {
            return extractChatTitle(tlAbsChat);
        }
        return null;
    }

    public static String extractChatTitle(TLAbsChat tlAbsChat) {
        if (tlAbsChat instanceof TLChannel)
            return ((TLChannel) tlAbsChat).getTitle();
        else if (tlAbsChat instanceof TLChat)
            return ((TLChat) tlAbsChat).getTitle();
        else if (tlAbsChat instanceof TLChatForbidden)
            return ((TLChatForbidden) tlAbsChat).getTitle();
        else if (tlAbsChat instanceof TLChannelForbidden)
            return ((TLChannelForbidden) tlAbsChat).getTitle();
        else if (tlAbsChat instanceof TLChatEmpty)
            return null;
        else
            LOG.error("unknown abstract chat for id {}. abstract chat class was {}", tlAbsChat.getId(), tlAbsChat.getClass());
        return null;
    }

    public static TLAbsChat getChat(TelegramClient telegramClient, int chatId) {
        //create vector with one element
        TLIntVector tlIntVector = new TLIntVector();
        tlIntVector.add(chatId);

        try {
            //Return first chat, if not empty
            TLVector<TLAbsChat> tlAbsChats = telegramClient.messagesGetChats(tlIntVector).getChats();
            if (tlAbsChats.isEmpty())
                return null;

            return tlAbsChats.get(0);
        } catch (RpcErrorException e) {
            LOG.error("error getting chat for id {}", chatId, e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
