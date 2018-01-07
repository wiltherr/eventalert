package de.haw.eventalert.source.telegram.client;

import com.github.badoualy.telegram.api.TelegramClient;
import com.github.badoualy.telegram.api.UpdateCallback;
import com.github.badoualy.telegram.tl.api.*;
import com.github.badoualy.telegram.tl.api.messages.TLAbsMessages;
import com.github.badoualy.telegram.tl.core.TLIntVector;
import com.github.badoualy.telegram.tl.exception.RpcErrorException;
import de.haw.eventalert.source.telegram.client.util.Chat;
import de.haw.eventalert.source.telegram.client.util.User;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * callback class for telegram events, to generate events on {@link TelegramEventListener}.
 * Implementation of the {@link UpdateCallback} interface.
 * <p>
 * note: currently only some updates are supported.
 */
public class TelegramEventCallback implements UpdateCallback {

    public static final Logger LOG = LoggerFactory.getLogger(TelegramEventCallback.class);
    private TelegramEventListener listener;

    public TelegramEventCallback(TelegramEventListener listener) {
        this.listener = listener;
    }

    public static TelegramAbstractEvent getTelegramAbstractEvent(TelegramClient telegramClient, int messageId) {
        //create vector with one element
        TLIntVector messageIds = new TLIntVector();
        messageIds.add(messageId);
        try {
            TLAbsMessages tlAbsMessages = telegramClient.messagesGetMessages(messageIds);
            LOG.debug("create telegramAbstractEvent. sizes of vectors: messages={}, chats={}, users={}", tlAbsMessages.getMessages().size(), tlAbsMessages.getChats().size(), tlAbsMessages.getUsers().size());

            //Fill the telegramAbstractEvent
            int fromUserId = -1;
            TelegramAbstractEvent telegramAbstractEvent = new TelegramAbstractEvent();
            if (!tlAbsMessages.getMessages().isEmpty()) {
                telegramAbstractEvent.setMessage(tlAbsMessages.getMessages().get(0));
                if (tlAbsMessages.getMessages().get(0) instanceof TLMessage) {
                    fromUserId = ((TLMessage) tlAbsMessages.getMessages().get(0)).getFromId();
                }
            }
            if (!tlAbsMessages.getChats().isEmpty())
                telegramAbstractEvent.setChat(tlAbsMessages.getChats().get(0));
            if (!tlAbsMessages.getUsers().isEmpty()) {
                int finalFromUserId = fromUserId;
                tlAbsMessages.getUsers().forEach(tlAbsUser -> {
                    if (tlAbsUser.getId() == finalFromUserId) {
                        telegramAbstractEvent.setUser(tlAbsUser);
                    }
                });
            }
            return telegramAbstractEvent;
        } catch (RpcErrorException e) {
            LOG.error("converting message failed: error on on get message. message id was {}", messageId, e);
        } catch (IOException e) {
            LOG.error("failed to generate abstract event", e);
        }


        return null;
    }

    public static TelegramMessageEvent getTelegramMessageEvent(TelegramClient telegramClient, TelegramAbstractEvent telegramAbstractEvent) {
        TelegramMessageEvent resultMessageEvent = new TelegramMessageEvent();
        if (telegramAbstractEvent.getMessage() instanceof TLMessage) {
            TLMessage tlMessage = (TLMessage) telegramAbstractEvent.getMessage();
            resultMessageEvent.setMessage(tlMessage.getMessage())
                    .setDate(tlMessage.getDate())
                    .setFromId(tlMessage.getFromId())
                    .setToId(User.getSelfId(telegramClient));
        }

        if (telegramAbstractEvent.getChat() != null) {
            resultMessageEvent.setGroupId(telegramAbstractEvent.getChat().getId())
                    .setGroup(Chat.extractChatTitle(telegramAbstractEvent.getChat()));
        }

        if (telegramAbstractEvent.getUser() instanceof TLUser) {
            TLUser tlUser = (TLUser) telegramAbstractEvent.getUser();
            resultMessageEvent.setFromId(tlUser.getId())
                    .setFrom(User.extractName(tlUser));
        }
        return resultMessageEvent;
    }

    @Override
    public void onUpdates(@NotNull TelegramClient telegramClient, @NotNull TLUpdates tlUpdates) {
        //Wird bei GIFs in Gruppenchats getriggert
        LOG.debug("TLUpdates called");

        if (tlUpdates.getUpdates().isEmpty())
            LOG.info("Update was empty");

        //process update
        tlUpdates.getUpdates().forEach(absUpdate -> this.processUpdate(absUpdate, telegramClient));

        //only for debug logging
        tlUpdates.getChats().forEach(tlAbsChat -> {
            if (tlAbsChat instanceof TLChat) {
                LOG.debug("Chat title was {} ", ((TLChat) tlAbsChat).getTitle());
            }
        });

        tlUpdates.getUsers().forEach(tlAbsUser -> {
            if (tlAbsUser instanceof TLUser) {
                if (((TLUser) tlAbsUser).getSelf())
                    LOG.debug("User was username: {}, firstname: {}", ((TLUser) tlAbsUser).getUsername(), ((TLUser) tlAbsUser).getFirstName());
            }
        });

    }

    @Override
    public void onUpdatesCombined(@NotNull TelegramClient telegramClient, @NotNull TLUpdatesCombined tlUpdatesCombined) {
        LOG.debug("TLUpdatesCombined called");
    }

    @Override
    public void onUpdateShort(@NotNull TelegramClient telegramClient, @NotNull TLUpdateShort tlUpdateShort) {
        LOG.debug("TLUpdateShort called");
        processUpdate(tlUpdateShort.getUpdate(), telegramClient);
    }

    @Override
    public void onShortChatMessage(@NotNull TelegramClient telegramClient, @NotNull TLUpdateShortChatMessage tlUpdate) {
        TelegramAbstractEvent telegramAbstractEvent = getTelegramAbstractEvent(telegramClient, tlUpdate.getId());
        LOG.debug("TLUpdateShortChatMessage called");
        listener.onMessage(telegramAbstractEvent);
        listener.onMessage(getTelegramMessageEvent(telegramClient, telegramAbstractEvent));
    }

    @Override
    public void onShortMessage(@NotNull TelegramClient telegramClient, @NotNull TLUpdateShortMessage tlUpdateShortMessage) {
        TelegramAbstractEvent telegramAbstractEvent = getTelegramAbstractEvent(telegramClient, tlUpdateShortMessage.getId());
        LOG.debug("TLUpdateShortMessage called");
        listener.onMessage(telegramAbstractEvent);
        listener.onMessage(getTelegramMessageEvent(telegramClient, telegramAbstractEvent));
    }

    @Override
    public void onShortSentMessage(@NotNull TelegramClient telegramClient, @NotNull TLUpdateShortSentMessage tlUpdateShortSentMessage) {
        LOG.debug("TLUpdateShortSentMessage called");
    }

    @Override
    public void onUpdateTooLong(@NotNull TelegramClient telegramClient) {
        LOG.debug("UpdateTooLong called");
    }

    private void processUpdate(TLAbsUpdate update, TelegramClient telegramClient) {
        if (update instanceof TLUpdateNewMessage) {
            TLAbsMessage message = ((TLUpdateNewMessage) update).getMessage();
            if (message instanceof TLMessage) {
                LOG.info("New message was {} ", ((TLMessage) message).getMessage());
                TelegramAbstractEvent event = getTelegramAbstractEvent(telegramClient, message.getId());
                listener.onMessage(event);
                listener.onMessage(getTelegramMessageEvent(telegramClient, event));
            }
        }
    }

    public interface TelegramEventListener {
        void onMessage(TelegramAbstractEvent telegramAbstractEvent);

        void onMessage(TelegramMessageEvent telegramMessageEvent);
    }


}

