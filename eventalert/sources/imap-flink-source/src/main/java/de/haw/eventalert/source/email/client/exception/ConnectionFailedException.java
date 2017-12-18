package de.haw.eventalert.source.email.client.exception;

import javax.mail.MessagingException;

/**
 * Created by Tim on 16.12.2017.
 */
public class ConnectionFailedException extends EMailSourceClientException {
    public ConnectionFailedException(String message, MessagingException cause) {
        super(message, cause);
    }

    public ConnectionFailedException(String message) {
        super(message);
    }
}
