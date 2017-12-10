package de.haw.eventalert.core.producer.telegram;

/**
 * Created by Tim on 01.10.2017.
 */
public interface TelegramMessage {
    String getMessage();

    String getFrom();

    int getFromId();

    String getTo();

    int getToId();

    String getGroup();

    int getGroupId();
}
