package de.haw.eventalert.source.email.client;

import com.icegreen.greenmail.server.AbstractServer;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.*;
import de.haw.eventalert.source.email.client.imap.EMailImapClient;
import de.haw.eventalert.source.email.configuration.EMailSourceConfiguration;
import de.haw.eventalert.source.email.entity.MailMessage;
import de.haw.eventalert.source.email.test.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class EMailClientsTest {

    //TODO add tests with different folders
    private static final String TEST_FOLDER_NAME = "INBOX";

    private GreenMail greenMail;
    private GreenMailUser greenMailUser;
    private EMailTestClient eMailTestClient;
    private ExecutorService clientExecutor;

    @AfterEach
    void tearDown() throws InterruptedException {
        cancelClientThread();
        stopGreenMail();
    }

    private void initGreenMail(ServerSetup serverSetup) {
        if (serverSetup.isSecure()) //provide greenmail dummy provider when testing ssl
            Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName());

        greenMail = new GreenMail(serverSetup);
        greenMailUser = greenMail.setUser(GreenMailUtil.random(), GreenMailUtil.random());
        greenMail.start();
    }

    private void stopGreenMail() {
        if (greenMail != null) {
            greenMail.stop();
        }
    }

    private void initClient(EMailClient eMailClient, AbstractServer greenMailServer) {
        EMailImapClient.IS_UNIT_TEST = true; //workaround for testing with greenmail
        eMailTestClient = new EMailTestClient(eMailClient);
        Properties props = TestUtil.generateEMailSourceProperties(greenMailServer, greenMailUser);
        eMailTestClient.setConfiguration(EMailSourceConfiguration.fromProperties(props));
    }

    private void runClientThread() throws InterruptedException {
        cancelClientThread();
        clientExecutor = Executors.newSingleThreadExecutor();
        clientExecutor.submit(eMailTestClient);
        boolean clientIsRunning = eMailTestClient.waitStartup(1000);
        Assertions.assertTrue(clientIsRunning);
    }

    private void cancelClientThread() throws InterruptedException {
        if (clientExecutor != null) {
            eMailTestClient.cancel();
            clientExecutor.awaitTermination(2, TimeUnit.MILLISECONDS);
        }
    }

    @Test
    public void testImapClient() throws InterruptedException {
        initGreenMail(ServerSetupTest.IMAP);
        testClient(greenMail.getImap(), EMailClients.createImap());
    }

    @Test
    public void testImapClientWithSSL() throws InterruptedException {
        initGreenMail(ServerSetupTest.IMAPS);
        testClient(greenMail.getImaps(), EMailClients.createImap());
    }


    @Test
    public void testImapClientReconnect() throws InterruptedException {
        initGreenMail(ServerSetupTest.IMAP);
        testClientReconnect(greenMail.getImap(), EMailClients.createImap(), 1000);
    }

    @Test
    public void testImapClientReconnectWithSSL() throws InterruptedException {
        initGreenMail(ServerSetupTest.IMAPS);
        testClientReconnect(greenMail.getImaps(), EMailClients.createImap(), 1000);
    }

//    @Test
//    public void testPop3Client() throws ExecutionFailedException, UserAuthFailedException, InterruptedException, IOException, MessagingException {
//        //initGreenMail(ServerSetupTest.POP3);
//        //TODO implement pop3 client
//        //testClient(greenMail.getPop3(), EMailClients.createImap());
//    }

    private void testClient(AbstractServer greenMailServer, EMailClient eMailClient) throws InterruptedException {
        initClient(eMailClient, greenMailServer);
        //use a list as consumer for the mailMessages
        List<MailMessage> actualConsumedMailMessages = new ArrayList<>();
        eMailTestClient.setConsumer(actualConsumedMailMessages::add);
        runClientThread();

        //deliver random messages to user TODO test real MimeMessages not only text mimeMessages!
        TestUtil.deliverRandomTextMessagesWithDelay(greenMailServer, greenMailUser, 100, 10);
        //wait 1 second to the client can reactive the message
        TimeUnit.MILLISECONDS.sleep(1000);

        //convert the messages delivered by greenMail to mailMessages
        List<MailMessage> expectedConsumedMailMessages = TestUtil.retrieveAsMailMessageList(greenMailServer, greenMailUser);

        //compare expected MailMessages with actual MailMessages
        Assertions.assertEquals(expectedConsumedMailMessages.size(), actualConsumedMailMessages.size());
        Assertions.assertIterableEquals(expectedConsumedMailMessages, actualConsumedMailMessages);
    }

    private void testClientReconnect(AbstractServer greenMailServer, EMailClient eMailClient, long disconnectTimeMs) throws InterruptedException {
        initClient(eMailClient, greenMailServer);
        //use a list as consumer for the mailMessages
        List<MailMessage> actualConsumedMailMessages = new ArrayList<>();
        eMailTestClient.setConsumer(actualConsumedMailMessages::add);
        runClientThread();

        TestUtil.deliverRandomTextMessagesWithDelay(greenMailServer, greenMailUser, 50, 10);
        List<MailMessage> expectedConsumedMailMessages = TestUtil.retrieveAsMailMessageList(greenMailServer, greenMailUser);
        //wait 1 second to the client can reactive the message
        TimeUnit.MILLISECONDS.sleep(1000);

        //shutdown greenmail and wait
        greenMail.stop();
        TimeUnit.MILLISECONDS.sleep(disconnectTimeMs);
        //restart greenmail and reset user
        greenMail.start();
        greenMailUser = greenMail.setUser(greenMailUser.getLogin(), greenMailUser.getPassword());
        //wait the client to reconnect
        TimeUnit.MILLISECONDS.sleep(2000);

        TestUtil.deliverRandomTextMessagesWithDelay(greenMailServer, greenMailUser, 50, 10);
        expectedConsumedMailMessages.addAll(TestUtil.retrieveAsMailMessageList(greenMailServer, greenMailUser));
        //wait 1 second to the client can reactive the message
        TimeUnit.MILLISECONDS.sleep(1000);

        //compare expected MailMessages with actual MailMessages
        Assertions.assertEquals(expectedConsumedMailMessages.size(), actualConsumedMailMessages.size());
        Assertions.assertIterableEquals(expectedConsumedMailMessages, actualConsumedMailMessages);

    }

}
