package de.haw.eventalert.source.telegram.api.auth.exception;

public class PhoneNumberNullOrEmptyException extends Exception {
    public PhoneNumberNullOrEmptyException() {
        super("phoneNumber is null or empty.");
    }
}
