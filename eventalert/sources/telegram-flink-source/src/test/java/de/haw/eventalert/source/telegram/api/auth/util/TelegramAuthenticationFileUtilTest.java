package de.haw.eventalert.source.telegram.api.auth.util;

import de.haw.eventalert.source.telegram.api.auth.TelegramAuthentication;
import de.haw.eventalert.source.telegram.test.TestConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;


public class TelegramAuthenticationFileUtilTest {

    private TelegramAuthentication testAuth;

    private Path testFilePath;

    @BeforeEach
    public void setUp() throws IOException {
        testFilePath = Paths.get(TestConstants.TEST_DATA_PATH, "testFile.test");

        byte[] testAuthKey = new byte[300];
        new Random().nextBytes(testAuthKey);

        testAuth = new TelegramAuthentication(testAuthKey, "127.0.0.1", 1234);
    }

    @AfterEach
    public void tearDown() throws IOException {
        //Files.deleteIfExists(testFilePath);
    }

    @Test
    void testWithExistingFile() throws IOException {
        assertTelegramAuthNotEmpty(testAuth);

        if (Files.notExists(testFilePath)) {
            Files.createFile(testFilePath);
        }

        TelegramAuthenticationFileUtil.writeToFile(testFilePath, testAuth);

        TelegramAuthentication resultAuth = TelegramAuthenticationFileUtil.readFromFile(testFilePath);

        assertTelegramAuthNotEmpty(resultAuth);
        assertTelegramAuthEquals(resultAuth, testAuth);
    }

    @Test
    void testWithNonExistingFile() throws IOException {
        assertTelegramAuthNotEmpty(testAuth);

        Files.deleteIfExists(testFilePath);

        TelegramAuthenticationFileUtil.writeToFile(testFilePath, testAuth);

        TelegramAuthentication resultAuth = TelegramAuthenticationFileUtil.readFromFile(testFilePath);

        assertTelegramAuthNotEmpty(resultAuth);
        assertTelegramAuthEquals(resultAuth, testAuth);
    }

    private void assertTelegramAuthNotEmpty(TelegramAuthentication telegramAuthStorage) {
        Assertions.assertNotNull(telegramAuthStorage.getAuthKey());
        Assertions.assertFalse(telegramAuthStorage.getAuthKey().length == 0);
        Assertions.assertNotNull(telegramAuthStorage.getDataCenter().getIp());
        Assertions.assertFalse(telegramAuthStorage.getDataCenter().getIp().isEmpty());
        Assertions.assertFalse(telegramAuthStorage.getDataCenter().getPort() == 0);
    }

    private void assertTelegramAuthEquals(TelegramAuthentication a, TelegramAuthentication b) {
        Assertions.assertEquals(a.getDataCenter().getIp(), b.getDataCenter().getIp());
        Assertions.assertEquals(a.getDataCenter().getPort(), b.getDataCenter().getPort());
        Assertions.assertArrayEquals(a.getAuthKey(), b.getAuthKey());
    }

}