package de.haw.eventalert.source.telegram.test.tool;

import de.haw.eventalert.source.telegram.api.ApiConfiguration;
import de.haw.eventalert.source.telegram.api.auth.TelegramAuthentication;
import de.haw.eventalert.source.telegram.api.auth.tool.CommandLineTelegramAuthenticator;
import de.haw.eventalert.source.telegram.api.auth.util.TelegramAuthenticationFileUtil;
import de.haw.eventalert.source.telegram.test.TestConstants;
import de.haw.eventalert.source.telegram.util.PropertyUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public class CreateTestAuthenticationTool {

    public static void main(String[] args) throws Exception {
        //load api configuration
        ApiConfiguration apiConfiguration;
        try {
            apiConfiguration = ApiConfiguration.fromProperties(PropertyUtil.getApiProperties());
        } catch (IOException e) {
            throw new Exception("api property file is missing or cant be load. check the path in PropertyUtil class", e);
        }

        if (Files.exists(TestConstants.TEST_AUTH_KEY_FILE_PATH)) {
            System.out.println("Test authentication key file already exists!");

            Scanner scanner = new Scanner(System.in);
            String userInput;
            do {
                System.out.println("Overwrite existing auth key file? (y / n)");
                userInput = scanner.nextLine();
                if (!"y".equals(userInput) || !"n".equals(userInput)) {
                    System.out.print("Only accepts y or n as input!");
                }
            } while (!"y".equals(userInput) || !"n".equals(userInput));
            if ("y".equals(userInput)) {
                System.out.println("Key file will be overwritten after successful authentication!");
            } else {
                System.out.println("Key file will not be overwirrten. exiting creation process.");
                System.exit(1);
            }
        }

        CommandLineTelegramAuthenticator tool = new CommandLineTelegramAuthenticator(apiConfiguration);
        TelegramAuthentication telegramAuthentication = tool.startCommandLineAuthDialog();
        TelegramAuthenticationFileUtil.writeToFile(TestConstants.TEST_AUTH_KEY_FILE_PATH, telegramAuthentication);
    }
}
