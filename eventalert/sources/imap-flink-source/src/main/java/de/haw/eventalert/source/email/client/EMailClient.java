package de.haw.eventalert.source.email.client;

import de.haw.eventalert.source.email.client.exception.ConnectionFailedException;
import de.haw.eventalert.source.email.client.exception.ExecutionFailedException;
import de.haw.eventalert.source.email.client.exception.UserAuthFailedException;
import de.haw.eventalert.source.email.configuration.EMailSourceConfiguration;
import de.haw.eventalert.source.email.entity.MailMessage;

import java.io.Serializable;
import java.util.function.Consumer;

public interface EMailClient extends Serializable {
    void setConfiguration(EMailSourceConfiguration configuration);

    void runClient() throws ExecutionFailedException, UserAuthFailedException, ConnectionFailedException;

    boolean waitStartup(long timeoutMillis) throws InterruptedException;

    void setConsumer(Consumer<MailMessage> consumer);

    void cancel();
}
