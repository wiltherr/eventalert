package de.haw.eventalert.source.telegram.api.auth.exception;

public class AuthProcessAlreadyStartedException extends IllegalStateException {
    public AuthProcessAlreadyStartedException() {
        super("the authentication process was already started!");
    }
}
