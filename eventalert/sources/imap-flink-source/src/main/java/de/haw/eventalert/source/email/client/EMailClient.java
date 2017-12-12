package de.haw.eventalert.source.email.client;

import de.haw.eventalert.source.email.client.exception.EMailSourceClientExecutionException;
import de.haw.eventalert.source.email.client.exception.EMailSourceClientLoginFailedException;

import java.io.Serializable;

public interface EMailClient extends EMailProducer, Serializable {
    void init(String protocol, String host, int port, String userName, String userPassword, String folderName);

    void login() throws EMailSourceClientLoginFailedException;

    void runClient() throws EMailSourceClientExecutionException;

    void cancel();
}
