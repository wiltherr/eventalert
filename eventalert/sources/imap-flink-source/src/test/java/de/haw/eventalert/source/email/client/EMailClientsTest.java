package de.haw.eventalert.source.email.client;

import com.icegreen.greenmail.server.AbstractServer;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.*;
import de.haw.eventalert.source.email.client.exception.ExecutionFailedException;
import de.haw.eventalert.source.email.client.exception.UserAuthFailedException;
import de.haw.eventalert.source.email.entity.MailMessage;
import de.haw.eventalert.source.email.test.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class EMailClientsTest {

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

        if (serverSetup.isSecure()) //provide greenmail dummy provider when testing ssl
            Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName());

        greenMail = new GreenMail(serverSetup);
        greenMailUser = greenMail.setUser(GreenMailUtil.random(), GreenMailUtil.random());
        greenMail.start();
    }

    private void initClient(EMailClient eMailClient, AbstractServer greenMailServer) {
        Assertions.assertNotNull(eMailClient);
        Assertions.assertNotNull(greenMailUser);
        Assertions.assertNotNull(greenMailServer);
        Assertions.assertNull(runnableEMailClient);
        runnableEMailClient = new RunnableEMailClient(eMailClient);

        String host = greenMailServer.getServerSetup().getBindAddress();
        int port = greenMailServer.getServerSetup().getPort();
        boolean isSSL = greenMailServer.getServerSetup().isSecure();

        String user = greenMailUser.getLogin();
        String password = greenMailUser.getPassword();

        runnableEMailClient.init(host, port, isSSL, user, password, TEST_FOLDER_NAME);
    }

    private void runClientThread() {
        Assertions.assertNotNull(runnableEMailClient);
        Thread thread = new Thread(runnableEMailClient);
        thread.start();
    }

    @Test
    public void testImapClient() throws ExecutionFailedException, UserAuthFailedException, InterruptedException, IOException, MessagingException {
        initGreenMail(ServerSetupTest.IMAP);
        testClient(greenMail.getImap(), EMailClients.createImap(), 100);
    }

    @Test
    public void testImapClientWithSSL() throws ExecutionFailedException, UserAuthFailedException, InterruptedException, IOException, MessagingException {
        initGreenMail(ServerSetupTest.IMAPS); //TODO schlägt fehl, warum?
        testClient(greenMail.getImaps(), EMailClients.createImap(), 1000); //SSL needs more time to connect
    }

//    @Test
//    public void testPop3Client() throws ExecutionFailedException, UserAuthFailedException, InterruptedException, IOException, MessagingException {
//        //initGreenMail(ServerSetupTest.POP3);
//        //TODO implement pop3 client
//        //testClient(greenMail.getPop3(), EMailClients.createImap());
//    }

    private void testClient(AbstractServer greenMailServer, EMailClient eMailClient, long clientStartWaitTime) throws UserAuthFailedException, InterruptedException {
        Assertions.assertNotNull(greenMailUser);
        Assertions.assertNotNull(greenMailServer);
        Assertions.assertNull(runnableEMailClient);

        initClient(eMailClient, greenMailServer);

        //use a list as consumer for the mailMessages
        List<MailMessage> actualConsumedMailMessages = new ArrayList<>();
        runnableEMailClient.setConsumer(actualConsumedMailMessages::add);

        runClientThread();
        //wait 1 second for client to start
        TimeUnit.MILLISECONDS.sleep(clientStartWaitTime);

        //deliver random messages to user TODO test real MimeMessages not only text mimeMessages!
        TestUtil.deliverRandomTextMessages(greenMailServer, greenMailUser, 10);
        //wait 1 second for client to start
        TimeUnit.MILLISECONDS.sleep(1000);

        //convert the messages delivered by greenMail to mailMessages
        List<MailMessage> expectedConsumedMailMessages = TestUtil.retrieveAsMailMessageList(greenMailServer, greenMailUser);

        //compare expected MailMessages with actual MailMessages
        Assertions.assertEquals(expectedConsumedMailMessages.size(), actualConsumedMailMessages.size());
        Assertions.assertIterableEquals(expectedConsumedMailMessages, actualConsumedMailMessages);
    }

}
