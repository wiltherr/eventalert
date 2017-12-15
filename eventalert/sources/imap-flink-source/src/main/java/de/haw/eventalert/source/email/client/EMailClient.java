package de.haw.eventalert.source.email.client;

import de.haw.eventalert.source.email.client.exception.ExecutionFailedException;
import de.haw.eventalert.source.email.client.exception.UserAuthFailedException;

import java.io.Serializable;

public interface EMailClient extends EMailProducer, Serializable {
    void init(String host, int port, boolean isSSL, String userName, String userPassword, String folderName);

    void runClient() throws ExecutionFailedException, UserAuthFailedException;

    void cancel();
}
