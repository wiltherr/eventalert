package de.haw.eventalert.source.telegram.api.auth.exception;

public class AuthenticationCodeNullOrEmptyException extends Exception {
    public AuthenticationCodeNullOrEmptyException() {
        super("authentication code is null or empty.");
    }
}
