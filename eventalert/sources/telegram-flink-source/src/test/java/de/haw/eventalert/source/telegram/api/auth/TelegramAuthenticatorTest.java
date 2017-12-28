package de.haw.eventalert.source.telegram.api.auth;

import de.haw.eventalert.source.telegram.api.ApiConfiguration;
import de.haw.eventalert.source.telegram.api.auth.exception.AuthProcessAlreadyStartedException;
import de.haw.eventalert.source.telegram.api.auth.exception.PhoneNumberInvalidException;
import de.haw.eventalert.source.telegram.api.auth.exception.PhoneNumberNullOrEmptyException;
import de.haw.eventalert.source.telegram.api.exception.TelegramApiErrorException;
import de.haw.eventalert.source.telegram.util.PropertyUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TelegramAuthenticatorTest {

    private static Properties apiProperties;

    private final String FAKE_PHONE_NUMBER_INVALID = "123456789";
    private final String FAKE_PHONE_NUMBER_VALID = "+79991234567";
    private TelegramAuthenticator telegramAuthenticator;

    @BeforeAll
    public static void setUpAll() throws IOException {
        apiProperties = PropertyUtil.getApiProperties();
    }

    @BeforeEach
    public void setUp() {
        ApiConfiguration apiConfiguration = ApiConfiguration.fromProperties(apiProperties);
        telegramAuthenticator = new TelegramAuthenticator(apiConfiguration);
    }

    @AfterEach
    public void tearDown() {
        telegramAuthenticator.close();
    }

    @Test
    public void testStartAuthentication() throws TelegramApiErrorException, PhoneNumberNullOrEmptyException, PhoneNumberInvalidException {
        assertThrows(PhoneNumberNullOrEmptyException.class, () -> telegramAuthenticator.startAuthGenerator(null));
        assertThrows(PhoneNumberNullOrEmptyException.class, () -> telegramAuthenticator.startAuthGenerator(""));
        assertThrows(PhoneNumberInvalidException.class, () -> telegramAuthenticator.startAuthGenerator(FAKE_PHONE_NUMBER_INVALID));

        TelegramAuthenticator.AuthGenerator authGenerator = telegramAuthenticator.startAuthGenerator(FAKE_PHONE_NUMBER_VALID);
        assertThrows(AuthProcessAlreadyStartedException.class, () -> telegramAuthenticator.startAuthGenerator(FAKE_PHONE_NUMBER_VALID));
    }

//    @Test
//    public void testWithTestAuthentication() {
//        //Test without calling startAuthGenerator before
//        assertThrows(IllegalStateException.class, () -> telegramAuthenticator.generateAuthentication("authCode"));
//
//        assertTrue(telegramAuthenticator.startAuthGenerator(FAKE_PHONE_NUMBER_VALID));
//        assertThrows(NullPointerException.class, () -> telegramAuthenticator.generateAuthentication(null));
//        assertThrows(IllegalArgumentException.class, () -> telegramAuthenticator.generateAuthentication(""));
//        assertNull(telegramAuthenticator.generateAuthentication("authCode"));
//        assertThrows(IllegalArgumentException.class, () -> telegramAuthenticator.generateAuthentication(""));
//
//        Test with real phone number and real telegram authentication
//
//        System.out.println("A authentication code was sent via sms or telegram to "+ REAL_TELEGRAM_TEST_PHONE_NUMBER +"." + System.lineSeparator()
//            + "Please enter the code correctly. otherwise following tests will fail!");
//        Scanner reader = new Scanner(System.in);
//        String authenticationCode = reader.nextLine();
//        TelegramAuthStorage apiStorageVolatile = telegramAuthenticator.generateAuthentication(authenticationCode);
//        assertNotNull(apiStorageVolatile);
//
//        //validate ApiStorage volatile
//        assertTrue(apiStorageVolatile.hasAuthKey());
//        assertTrue(apiStorageVolatile.getAuthKey().length > 0);
//        assertNotNull(apiStorageVolatile.loadAuthKey());
//        assertNotNull(apiStorageVolatile.loadDc());
//        assertNull(apiStorageVolatile.loadSession());
//
//        //validate telegram authentication
//        AuthKey telegramAuthKey = apiStorageVolatile.loadAuthKey();
//        assertNotNull(telegramAuthKey.getKey());
//        assertTrue(telegramAuthKey.getKey().length > 0);
//        assertTrue(telegramAuthKey.getKeyId().length > 0);
//        assertTrue(telegramAuthKey.getKeyIdAsLong() > 0);
//
//        //test delete apiStorage
//        apiStorageVolatile.deleteAuthKey();
//        apiStorageVolatile.deleteDc();
//        assertFalse(apiStorageVolatile.hasAuthKey());
//        assertNull(apiStorageVolatile.getAuthKey());
//        assertNull(apiStorageVolatile.loadAuthKey().getKey());
//    }

}
