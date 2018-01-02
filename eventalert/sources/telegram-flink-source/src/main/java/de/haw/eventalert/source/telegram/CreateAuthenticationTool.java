package de.haw.eventalert.source.telegram;

import de.haw.eventalert.source.telegram.api.ApiConfiguration;
import de.haw.eventalert.source.telegram.api.auth.TelegramAuthentication;
import de.haw.eventalert.source.telegram.api.auth.tool.CommandLineTelegramAuthenticator;
import de.haw.eventalert.source.telegram.api.auth.util.TelegramAuthenticationFileUtil;
import de.haw.eventalert.source.telegram.util.PropertyUtil;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class CreateAuthenticationTool {

    public static final String DEFAULT_API_AUTH_KEY_FILE_NAME = "default-telegram-auth.storage";

    public static void main(String[] args) throws Exception {
        Path authKeyPath;
        if (StringUtils.isBlank(args[0])) {
            if (CreateAuthenticationTool.class.getClassLoader().getResource(DEFAULT_API_AUTH_KEY_FILE_NAME) != null) {
                authKeyPath = Paths.get(CreateAuthenticationTool.class.getClassLoader().getResource(DEFAULT_API_AUTH_KEY_FILE_NAME).toURI());
            } else {
                throw new Exception("resource path not found");
            }
        } else {
            authKeyPath = Paths.get(args[0]);
        }

        //load api configuration
        ApiConfiguration apiConfiguration;
        try {
            apiConfiguration = ApiConfiguration.fromProperties(PropertyUtil.getApiProperties());
        } catch (IOException e) {
            throw new Exception("api property file is missing or cant be load. check the path in PropertyUtil class", e);
        }

        if (Files.exists(authKeyPath)) {
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
        TelegramAuthenticationFileUtil.writeToFile(authKeyPath, telegramAuthentication);
    }
}
