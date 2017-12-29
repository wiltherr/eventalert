package de.haw.eventalert.source.email.client.exception;

public class ExecutionFailedException extends EMailSourceClientException {
    public ExecutionFailedException(Throwable cause) {
        super(cause);
    }

    public ExecutionFailedException(String message) {
        super(message);
    }

    public ExecutionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
