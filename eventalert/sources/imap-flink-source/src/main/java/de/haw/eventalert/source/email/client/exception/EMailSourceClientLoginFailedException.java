package de.haw.eventalert.source.email.client.exception;

public class EMailSourceClientLoginFailedException extends Exception {
    public EMailSourceClientLoginFailedException(Throwable cause) {
        super(cause);
    }

    public EMailSourceClientLoginFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public EMailSourceClientLoginFailedException(String message) {
        super(message);
    }
}
