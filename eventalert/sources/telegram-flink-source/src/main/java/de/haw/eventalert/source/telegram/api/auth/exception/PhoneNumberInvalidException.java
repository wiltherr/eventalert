package de.haw.eventalert.source.telegram.api.auth.exception;

public class PhoneNumberInvalidException extends Exception {
    public PhoneNumberInvalidException() {
        super("telegram api refuses phone number. maybe phone number is invalid or not registered.");
    }
}
