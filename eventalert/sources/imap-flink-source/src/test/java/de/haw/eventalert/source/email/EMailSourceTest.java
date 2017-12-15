package de.haw.eventalert.source.email;

import com.icegreen.greenmail.server.AbstractServer;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.util.ServerSetupTest;
import de.haw.eventalert.source.email.configuration.EMailSourceConfiguration;
import de.haw.eventalert.source.email.entity.MailMessage;
import de.haw.eventalert.source.email.test.SimpleTestJobThread;
import de.haw.eventalert.source.email.test.TestUtil;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class EMailSourceTest {

    static final GreenMail greenMail = new GreenMail(ServerSetupTest.IMAP);
    static final GreenMailUser greenMailUser = greenMail.setUser(GreenMailUtil.random(), GreenMailUtil.random());
    static final List<MailMessage> receivedTestSinkMessage = new ArrayList<>();
    static final Thread testJob;

    static {
        Properties props = generateEMailSourceProperties(greenMail.getImap(), greenMailUser);
        testJob = new SimpleTestJobThread<>(new EMailSource(props),
                (SinkFunction<MailMessage>) receivedTestSinkMessage::add);
    }

    @BeforeAll
    static void setUpAll() throws InterruptedException {
        greenMail.start();

        testJob.start();
        TimeUnit.SECONDS.sleep(2); //wait some time, so the job can be started
    }

    @AfterAll
    static void tearDownAll() {
        testJob.interrupt();
        greenMail.stop();
    }

    private static Properties generateEMailSourceProperties(AbstractServer server, GreenMailUser user) {
        ServerSetup serverSetup = server.getServerSetup();
        Properties props = new Properties();
        //random source id
        props.setProperty(EMailSourceConfiguration.PROPERTY_KEY_SOURCE_ID, String.valueOf(new Random().nextInt()));
        //set properties from greenMail server setup
        props.setProperty(EMailSourceConfiguration.PROPERTY_KEY_EMAIL_SERVER_IP, serverSetup.getBindAddress());
        props.setProperty(EMailSourceConfiguration.PROPERTY_KEY_EMAIL_SERVER_PORT, String.valueOf(serverSetup.getPort()));
        props.setProperty(EMailSourceConfiguration.PROPERTY_KEY_EMAIL_SERVER_SECURE, String.valueOf(serverSetup.isSecure()));
        //set properties from greenMailUser
        props.setProperty(EMailSourceConfiguration.PROPERTY_KEY_EMAIL_LOGIN_USER, user.getLogin());
        props.setProperty(EMailSourceConfiguration.PROPERTY_KEY_EMAIL_LOGIN_PASSWORD, user.getPassword());
        props.setProperty(EMailSourceConfiguration.PROPERTY_KEY_EMAIL_FOLDER, "INBOX");
        return props;
    }

    @BeforeEach
    void setUp() {
        receivedTestSinkMessage.clear();
    }

    @Test
    void testEMailSource() throws IOException, InterruptedException {
        int messageCount = 5;
        //deliver emails via greenMail
        TestUtil.deliverRandomTextMessages(greenMail.getImap(), greenMailUser, messageCount);
        TimeUnit.MILLISECONDS.sleep(messageCount * 200);
        List<MailMessage> expectedMessages = TestUtil.retrieveAsMailMessageList(greenMail.getImap(), greenMailUser);
        Assertions.assertIterableEquals(expectedMessages, receivedTestSinkMessage);
    }
}
