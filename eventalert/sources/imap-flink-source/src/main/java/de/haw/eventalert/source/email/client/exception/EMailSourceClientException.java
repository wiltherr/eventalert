package de.haw.eventalert.source.email.client.exception;

public class EMailSourceClientException extends Exception {
    public EMailSourceClientException(Throwable cause) {
        super("client failed", cause);
    }

    public EMailSourceClientException(String message) {
        super(message);
    }

    public EMailSourceClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
