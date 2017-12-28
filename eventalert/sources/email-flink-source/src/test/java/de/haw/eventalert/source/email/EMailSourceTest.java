package de.haw.eventalert.source.email;

import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import de.haw.eventalert.source.email.client.imap.EMailImapClient;
import de.haw.eventalert.source.email.entity.MailMessage;
import de.haw.eventalert.source.email.test.SimpleTestJobThread;
import de.haw.eventalert.source.email.test.TestUtil;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class EMailSourceTest {

    private static final String testUser = GreenMailUtil.random();
    private static final String testPwd = GreenMailUtil.random();
    private static final GreenMail greenMail = new GreenMail(ServerSetupTest.IMAP);
    private static final List<MailMessage> receivedTestSinkMessage = new ArrayList<>();
    private static final Thread testJob;
    private static GreenMailUser greenMailUser = greenMail.setUser(testUser, testPwd);

    static {
        Properties props = TestUtil.generateEMailSourceProperties(greenMail.getImap(), greenMailUser);
        testJob = new SimpleTestJobThread<>(new EMailSource(props), receivedTestSinkMessage::add);
    }

    @BeforeAll
    static void setUpAll() throws InterruptedException {
        EMailImapClient.IS_UNIT_TEST = true; //workaround for testing with greenmail
        greenMail.start();
        testJob.start();
        TimeUnit.SECONDS.sleep(2); //wait 2 seconds, so the job can be started
    }

    @AfterAll
    static void tearDownAll() throws InterruptedException {
        testJob.interrupt();
        greenMail.stop();
    }

    private static void resetGreenmail() throws InterruptedException {
        greenMail.reset();
        greenMailUser = greenMail.setUser(testUser, testPwd);
        TimeUnit.SECONDS.sleep(2); //wait 2 second, so the testJob has time to reconnect
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        //clear received sink messages for next test
        receivedTestSinkMessage.clear();
        //reset greenMail and recreate the user, so every test has a clean mailbox
        resetGreenmail();
    }

    @Test
    void testEMailSource() throws IOException, InterruptedException {
        int messageCount = 100;
        //deliver emails via greenMail
        TestUtil.deliverRandomTextMessagesWithDelay(greenMail.getImap(), greenMailUser, messageCount, 10);
        List<MailMessage> expectedMessages = TestUtil.retrieveAsMailMessageList(greenMail.getImap(), greenMailUser);
        TimeUnit.SECONDS.sleep(1); //wait one second to ensure that all messages can be received
        Assertions.assertIterableEquals(expectedMessages, receivedTestSinkMessage);
    }

    @Test
    void testEMailSourceWithLongDelay() throws IOException, InterruptedException {
        int messageCount = 5;
        //deliver emails via greenMail
        TestUtil.deliverRandomTextMessagesWithDelay(greenMail.getImap(), greenMailUser, messageCount, 2000);
        List<MailMessage> expectedMessages = TestUtil.retrieveAsMailMessageList(greenMail.getImap(), greenMailUser);
        Assertions.assertIterableEquals(expectedMessages, receivedTestSinkMessage);
    }
}
