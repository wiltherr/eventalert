package de.haw.eventalert.source.telegram.client;

import java.io.Serializable;

/**
 * Created by Tim on 02.10.2017.
 */
public class TelegramMessageEvent implements Serializable {
    public static final String EVENT_TYPE = "TelegramMessage";
    private String message;
    private String from;
    private int fromId;
    private int toId;
    private int groupId;

    private String group;
    private int date;

    public TelegramMessageEvent() {

    }

    public int getDate() {
        return date;
    }

    public TelegramMessageEvent setDate(int date) {
        this.date = date;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public TelegramMessageEvent setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public TelegramMessageEvent setFrom(String from) {
        this.from = from;
        return this;
    }

    public int getFromId() {
        return fromId;
    }

    public TelegramMessageEvent setFromId(int fromId) {
        this.fromId = fromId;
        return this;
    }

    public int getToId() {
        return toId;
    }

    public TelegramMessageEvent setToId(int toId) {
        this.toId = toId;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public TelegramMessageEvent setGroup(String group) {
        this.group = group;
        return this;
    }

    public int getGroupId() {
        return groupId;
    }

    public TelegramMessageEvent setGroupId(int groupId) {
        this.groupId = groupId;
        return this;
    }

    @Override
    public String toString() {
        return "TelegramMessageEvent{" +
                "message='" + message + '\'' +
                ", from='" + from + '\'' +
                ", fromId=" + fromId +
                ", toId=" + toId +
                ", groupId=" + groupId +
                ", group='" + group + '\'' +
                ", date=" + date +
                '}';
    }
}
