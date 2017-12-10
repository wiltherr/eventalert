package de.haw.eventalert.core.producer.telegram;

/**
 * Created by Tim on 01.10.2017.
 */
public class TelegramMessageImpl implements TelegramMessage {
    private String message;
    private String from;
    private int fromId;
    private String to;
    private String groupId;

    private String group;

    private int toId;

    public TelegramMessageImpl(String message, String from, int fromId, String to, int toId, String group, String groupId) {
        this.message = message;
        this.from = from;
        this.fromId = fromId;
        this.to = to;
        this.toId = toId;
        this.group = group;
        this.groupId = groupId;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public TelegramMessageImpl setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String getFrom() {
        return from;
    }

    public TelegramMessageImpl setFrom(String from) {
        this.from = from;
        return this;
    }

    @Override
    public int getFromId() {
        return fromId;
    }

    public TelegramMessageImpl setFromId(int fromId) {
        this.fromId = fromId;
        return this;
    }

    @Override
    public String getTo() {
        return to;
    }

    public TelegramMessageImpl setTo(String to) {
        this.to = to;
        return this;
    }

    @Override
    public int getToId() {
        return toId;
    }

    public TelegramMessageImpl setToId(int toId) {
        this.toId = toId;
        return this;
    }

    @Override
    public String getGroup() {
        return group;
    }

    public TelegramMessageImpl setGroup(String group) {
        this.group = group;
        return this;
    }

    @Override
    public int getGroupId() {
        return getGroupId();
    }

    public TelegramMessageImpl setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }
}
