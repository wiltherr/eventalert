package de.haw.eventalert.source.telegram.api.auth.tool;

import com.github.badoualy.telegram.api.Kotlogram;
import de.haw.eventalert.source.telegram.api.ApiConfiguration;
import de.haw.eventalert.source.telegram.api.auth.TelegramAuthentication;
import de.haw.eventalert.source.telegram.api.auth.TelegramAuthenticator;
import de.haw.eventalert.source.telegram.api.auth.exception.AuthenticationCodeNullOrEmptyException;
import de.haw.eventalert.source.telegram.api.auth.exception.PhoneNumberInvalidException;
import de.haw.eventalert.source.telegram.api.auth.exception.PhoneNumberNullOrEmptyException;
import de.haw.eventalert.source.telegram.api.exception.TelegramApiErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class CommandLineTelegramAuthenticator {

    private final TelegramAuthenticator telegramAuthenticator;
    private final Scanner scanner;

    public CommandLineTelegramAuthenticator(ApiConfiguration apiConfiguration) {
        //create telegramAuthenticator
        telegramAuthenticator = new TelegramAuthenticator(apiConfiguration);
        //create scanner
        scanner = new Scanner(System.in);
    }

    public TelegramAuthentication startCommandLineAuthDialog() throws Exception {
        Logger kotlogramLogger = LoggerFactory.getLogger(Kotlogram.class);

        print("===== Telegram scanner authentication =====");
        print("Enter a valid phone number registered by telegram first.");

        String phoneNumber;
        TelegramAuthenticator.AuthGenerator authGenerator = null;
        do {
            print("phone number: ");
            phoneNumber = scanner.nextLine();
            try {
                authGenerator = telegramAuthenticator.startAuthGenerator(phoneNumber);
            } catch (PhoneNumberNullOrEmptyException e) {
                print("phone number was not empty. Please try again.");
            } catch (PhoneNumberInvalidException e) {
                print("%s!%n Please try again.", e.getMessage());
            } catch (TelegramApiErrorException e) {
                print("telegram api error: %s%n Please try again.", e.getMessage());
            }
        } while (authGenerator == null);

        print("a authentication code was send to %s via telegram or sms!", phoneNumber);
        print("please enter the received authentication code.");

        String authCode;
        TelegramAuthentication telegramAuthentication = null;
        do {
            print("authentication code:");
            authCode = scanner.nextLine();
            try {
                telegramAuthentication = authGenerator.generateAuthentication(authCode);
            } catch (AuthenticationCodeNullOrEmptyException e) {
                print("authentication code was empty. Please try again.");
            } catch (TelegramApiErrorException e) {
                print("telegram api error code %s%n", e.getMessage());
                print("Please try again or restart scanner authentication!");
            }
        } while (telegramAuthentication == null);

        print("authentication was successfully created.");
        this.close();
        return telegramAuthentication;
    }

    public void close() {
        telegramAuthenticator.close();
    }

    private void print(String message, Object... args) {
        System.out.println(String.format(message + "%n", args));
    }
}
