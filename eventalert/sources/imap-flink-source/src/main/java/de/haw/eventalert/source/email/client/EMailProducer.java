package de.haw.eventalert.source.email.client;

import de.haw.eventalert.source.email.entity.MailMessage;

import java.util.function.Consumer;

public interface EMailProducer {
    void setConsumer(Consumer<MailMessage> consumer);
}
