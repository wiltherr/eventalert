package de.haw.eventalert.source.email.client.exception;

public class EMailSourceClientExecutionException extends EMailSourceClientException {
    public EMailSourceClientExecutionException(Throwable cause) {
        super(cause);
    }

    public EMailSourceClientExecutionException(String message) {
        super(message);
    }

    public EMailSourceClientExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
