package de.haw.eventalert.source.email.client.exception;

public class UserAuthFailedException extends Exception {
    public UserAuthFailedException(Throwable cause) {
        super(cause);
    }

    public UserAuthFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAuthFailedException(String message) {
        super(message);
    }
}
