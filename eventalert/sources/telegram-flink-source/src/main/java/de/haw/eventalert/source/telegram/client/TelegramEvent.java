package de.haw.eventalert.source.telegram.client;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Tim on 27.09.2017.
 */
public class TelegramEvent {
    private String message;

    private Long time;

    private String sendingUserName;

    private Integer sendingUserId;
    private String receivingUserName;

    private Integer receivingUserId;
    private Integer groupId;

    private String groupName;
    private boolean selfMessage;

    public String getMessage() {
        return message;
    }

    public TelegramEvent setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getSendingUserName() {
        return sendingUserName;
    }

    public TelegramEvent setSendingUserName(String sendingUserName) {
        this.sendingUserName = sendingUserName;
        return this;
    }

    public Integer getSendingUserId() {
        return sendingUserId;
    }

    public TelegramEvent setSendingUserId(int sendingUserId) {
        this.sendingUserId = sendingUserId;
        return this;
    }

    public String getReceivingUserName() {
        return receivingUserName;
    }

    public TelegramEvent setReceivingUserName(String receivingUserName) {
        this.receivingUserName = receivingUserName;
        return this;
    }

    public Integer getReceivingUserId() {
        return receivingUserId;
    }

    public TelegramEvent setReceivingUserId(Integer receivingUserId) {
        this.receivingUserId = receivingUserId;
        return this;
    }

    public String getGroupName() {
        return groupName;
    }

    public TelegramEvent setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public TelegramEvent setGroupId(Integer groupId) {
        this.groupId = groupId;
        return this;
    }

    public boolean isSelfMessage() {
        return selfMessage;
    }

    public TelegramEvent setSelfMessage(boolean selfMessage) {
        this.selfMessage = selfMessage;
        return this;
    }

    public Long getTime() {
        return time;
    }

    public TelegramEvent setTime(Long time) {
        this.time = time;
        return this;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.GERMAN);
        String date = sdf.format(time * 1000);

        return "TelegramEvent{" +
                "message='" + message + '\'' +
                ", time='" + date + '\'' +
                ", sendingUserName='" + sendingUserName + '\'' +
                ", sendingUserId='" + sendingUserId + '\'' +
                ", receivingUserName='" + receivingUserName + '\'' +
                ", receivingUserId='" + receivingUserId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupId='" + groupId + '\'' +
                ", selfMessage=" + selfMessage +
                '}';
    }
}
