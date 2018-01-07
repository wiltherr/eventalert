package de.haw.eventalert.source.telegram.client;

import com.github.badoualy.telegram.tl.api.*;

/**
 * Created by Tim on 01.10.2017.
 */
public class TelegramAbstractEvent {
    private TLAbsMessage message;
    private TLAbsChat chat;
    private TLAbsUser user;

    public TelegramAbstractEvent() {

    }

    public TLAbsMessage getMessage() {
        return message;
    }

    public TelegramAbstractEvent setMessage(TLAbsMessage message) {
        this.message = message;
        return this;
    }

    public TLAbsChat getChat() {
        return chat;
    }

    public TelegramAbstractEvent setChat(TLAbsChat chat) {
        this.chat = chat;
        return this;
    }

    public TLAbsUser getUser() {
        return user;
    }

    public TelegramAbstractEvent setUser(TLAbsUser user) {
        this.user = user;
        return this;
    }

    @Override
    public String toString() {
        String title = null, message = null, user = null;
        if (this.message instanceof TLMessage) {
            message = ((TLMessage) this.message).getMessage();
        }
        if (chat instanceof TLChat) {
            title = ((TLChat) chat).getTitle();
        }
        if (this.user instanceof TLUser) {
            user = ((TLUser) this.user).getFirstName();
        }
        return "TelegramAbstractEvent{" +
                "message=" + message +
                ", chat=" + title +
                ", users=" + user +
                '}';
    }
}
