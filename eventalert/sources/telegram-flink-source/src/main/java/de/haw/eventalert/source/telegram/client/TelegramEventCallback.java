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
 *     note: currently only some updates are supported.
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
            e.printStackTrace();
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
                    //.setFrom(User.getName(telegramClient,tlMessage.getFromId()))
                    .setToId(User.getSelfId(telegramClient));

//            if(tlMessage.getToId() instanceof TLPeerUser) {
//                LOG.debug("TLMessage toId was TLPeerUser");
//                telegramMessageEvent.setToId(User.getName(telegramClient,((TLPeerUser) tlMessage.getToId()).getUserId()))
//                    .setToId(((TLPeerUser) tlMessage.getToId()).getUserId());
//            } else if(tlMessage.getToId() instanceof TLPeerChat) {
//                LOG.debug("TLMessage toId was TLPeerChat (setting toId and to with Chat-Title)");
//                //telegramMessageEvent.setToId(User.getName)
//                telegramMessageEvent.setToId(((TLPeerChat) tlMessage.getToId()).getChatId())
//                        .setToNumber(Chat.getTitle(telegramClient,((TLPeerChat) tlMessage.getToId()).getChatId()));
//
//            } else {
//                LOG.warn("TLMessage toId was not instance of TLPeerUser or TLPeerChat! It was instance of {}. use fallback selfUser!", tlMessage.getToId().getClass().getName());
//                telegramMessageEvent.setToId(User.getSelfName(telegramClient))
//                        .setToId(User.getSelfId(telegramClient));
//            }
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

        tlUpdates.getUpdates().forEach(absUpdate -> this.processUpdate(absUpdate, telegramClient));
        tlUpdates.getChats().forEach(tlAbsChat -> {
            if (tlAbsChat instanceof TLChat) {
                LOG.info("Chat title was {} ", ((TLChat) tlAbsChat).getTitle());
            }
        });

        tlUpdates.getUsers().forEach(tlAbsUser -> {
            if (tlAbsUser instanceof TLUser) {
                if (((TLUser) tlAbsUser).getSelf())
                    LOG.info("User was username: {}, firstname: {}", ((TLUser) tlAbsUser).getUsername(), ((TLUser) tlAbsUser).getFirstName());
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
        //Wird in gruppen getriggert
//        TelegramEvent event = new TelegramEvent();
//        //Setting Time & Message
//        event
//                .setTime((long) tlUpdate.getDate())
//                .setMessage(tlUpdate.getTelegramAbstractEvent());
//
//
//        //Setting SendingUser
//        String userName = User.getName(telegramClient, tlUpdate.getFromId());
//        event.setSendingUserName(userName)
//                .setSendingUserId(tlUpdate.getFromId());
//        //Setting Group
//        String groupName = Chat.getTitle(telegramClient, tlUpdate.getChatId());
//        event.setGroupId(tlUpdate.getChatId())
//                .setGroupName(groupName);
//        //Setting ReceivingUser
//        String selfName = User.getSelfName(telegramClient);
//        Integer selfId = User.getSelfId(telegramClient);
//        event.setReceivingUserName(selfName)
//                .setReceivingUserId(selfId);
//
//        if(event.getReceivingUserId().equals(event.getSendingUserId()))
//            event.setSelfMessage(true);

        TelegramAbstractEvent telegramAbstractEvent = getTelegramAbstractEvent(telegramClient, tlUpdate.getId());
        LOG.debug("TLUpdateShortChatMessage called");
        listener.onMessage(telegramAbstractEvent);
        listener.onMessage(getTelegramMessageEvent(telegramClient, telegramAbstractEvent));
    }

    @Override
    public void onShortMessage(@NotNull TelegramClient telegramClient, @NotNull TLUpdateShortMessage tlUpdateShortMessage) {
        //Wird in einzelnen chats getriggert
//        TelegramEvent event = new TelegramEvent();
//
//        event.setMessage(tlUpdateShortMessage.getMessage())
//                .setTime((long) tlUpdateShortMessage.getDate());
//
//        String userName = User.getName(telegramClient, tlUpdateShortMessage.getUserId());
//        event.setSendingUserName(userName)
//                .setSendingUserId(tlUpdateShortMessage.getUserId());
//        event.setReceivingUserName(User.getSelfName(telegramClient))
//                .setReceivingUserId(User.getSelfId(telegramClient));
//
//        if(event.getReceivingUserId().equals(event.getSendingUserId()))
//            event.setSelfMessage(true);

        TelegramAbstractEvent telegramAbstractEvent = getTelegramAbstractEvent(telegramClient, tlUpdateShortMessage.getId());
        LOG.debug("TLUpdateShortMessage called");
        listener.onMessage(telegramAbstractEvent);
        listener.onMessage(getTelegramMessageEvent(telegramClient, telegramAbstractEvent));
        //LOG.debug("ALT Message: {} ", Message.getMessage(telegramClient,tlUpdateShortMessage.getId()));
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

