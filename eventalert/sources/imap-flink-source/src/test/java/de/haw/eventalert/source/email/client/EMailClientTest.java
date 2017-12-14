package de.haw.eventalert.source.email.client;

import com.icegreen.greenmail.server.AbstractServer;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.util.ServerSetupTest;
import de.haw.eventalert.source.email.client.exception.EMailSourceClientExecutionException;
import de.haw.eventalert.source.email.client.exception.EMailSourceClientLoginFailedException;
import de.haw.eventalert.source.email.client.test.TestUtil;
import de.haw.eventalert.source.email.entity.MailMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class EMailClientTest {

    private static final String TEST_HOST = "";
    private static final String TEST_PORT = "";
    private static final String TEST_USER = "";

    //TODO add tests with different folders
    private static final String TEST_FOLDER_NAME = "INBOX";

    private GreenMail greenMail;
    private GreenMailUser greenMailUser;
    private RunnableEMailClient runnableEMailClient;

    @AfterEach
    void tearDown() {
        if (runnableEMailClient != null)
            runnableEMailClient.cancel();
        if (greenMailUser != null)
            greenMail.stop();
    }

    private void initGreenMail(ServerSetup serverSetup) {
        if (greenMail != null)
            greenMail.stop();
        greenMail = new GreenMail(serverSetup);
        greenMailUser = greenMail.setUser("test@email.com", GreenMailUtil.random(), GreenMailUtil.random());
        greenMail.start();
    }

    private void initClient(EMailClient eMailClient) {
        Assertions.assertNotNull(greenMailUser);
        Assertions.assertNull(runnableEMailClient);
        runnableEMailClient = new RunnableEMailClient(eMailClient);
        String host = greenMail.getImap().getServerSetup().getBindAddress();
        int port = greenMail.getImap().getServerSetup().getPort();
        String user = greenMailUser.getLogin();
        String password = greenMailUser.getPassword();
        runnableEMailClient.init("imap", host, port, user, password, TEST_FOLDER_NAME);
    }

    private void runClientThread() throws EMailSourceClientLoginFailedException {
        Assertions.assertNotNull(runnableEMailClient);
        runnableEMailClient.login();
        Thread thread = new Thread(runnableEMailClient);
        thread.start();
    }

    @Test
    public void testImap() throws EMailSourceClientExecutionException, EMailSourceClientLoginFailedException, InterruptedException, IOException, MessagingException {
        initGreenMail(ServerSetupTest.IMAP);
        initClient(EMailClients.createImap());
        testClient(greenMail.getImap());
    }

    @Test
    public void testPop3() throws EMailSourceClientExecutionException, EMailSourceClientLoginFailedException, InterruptedException, IOException, MessagingException {
        initGreenMail(ServerSetupTest.POP3);
        initClient(EMailClients.createImap()); //TODO implement pop3 client
        testClient(greenMail.getImap());
    }

    private void testClient(AbstractServer greenMailServer) throws EMailSourceClientLoginFailedException, InterruptedException {
        Assertions.assertNotNull(runnableEMailClient);
        Assertions.assertNotNull(greenMailUser);
        Assertions.assertNotNull(greenMailServer);

        //use a list as consumer for the mailMessages
        List<MailMessage> actualConsumedMailMessages = new ArrayList<>();
        runnableEMailClient.setConsumer(actualConsumedMailMessages::add);

        runClientThread();

        //deliver random messages to user TODO test real MimeMessages not only text mimeMessages!
        TestUtil.deliverRandomTextMessages(greenMailServer, greenMailUser, 10);

        //wait 1 second for receiving the messages
        TimeUnit.SECONDS.sleep(1);

        //convert the messages delivered by greenMail to mailMessages
        Message[] receivedMessages = TestUtil.retrieveMessages(greenMailServer, greenMailUser);
        List<MailMessage> expectedConsumedMailMessages = Stream.of(receivedMessages).map(MessageConverter.toMailMessage).collect(Collectors.toList());

        //compare expected MailMessages with actual MailMessages
        Assertions.assertEquals(expectedConsumedMailMessages.size(), actualConsumedMailMessages.size());
        for (int i = 0; i < expectedConsumedMailMessages.size(); i++) {
            Assertions.assertEquals(expectedConsumedMailMessages.get(i), actualConsumedMailMessages.get(i));
        }
    }

}
