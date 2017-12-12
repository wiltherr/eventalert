package de.haw.eventalert.source.email.client;

import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import de.haw.eventalert.source.email.client.exception.EMailSourceClientExecutionException;
import de.haw.eventalert.source.email.client.exception.EMailSourceClientLoginFailedException;
import de.haw.eventalert.source.email.client.test.TestUtil;
import de.haw.eventalert.source.email.entity.MailMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
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

    @BeforeEach
    void setUp() {
        greenMail = new GreenMail(ServerSetupTest.SMTP_IMAP);
        greenMailUser = greenMail.setUser("test@email.com", GreenMailUtil.random(), GreenMailUtil.random());
        greenMail.start();
    }

    @AfterEach
    void tearDown() {
        runnableEMailClient.cancel();
        greenMail.stop();
    }

    private void initClient(EMailClient eMailClient, Consumer<MailMessage> mailMessageConsumer) {
        runnableEMailClient = new RunnableEMailClient(eMailClient);
        String host = greenMail.getImap().getServerSetup().getBindAddress();
        int port = greenMail.getImap().getServerSetup().getPort();
        String user = greenMailUser.getLogin();
        String password = greenMailUser.getPassword();
        runnableEMailClient.init("imap", host, port, user, password, TEST_FOLDER_NAME);
        runnableEMailClient.setConsumer(mailMessageConsumer);
    }

    private void runClientThread() {
        Thread thread = new Thread(runnableEMailClient);
        thread.start();
    }

    @Test
    public void testImap() throws EMailSourceClientExecutionException, EMailSourceClientLoginFailedException, InterruptedException, IOException, MessagingException {
        //use list as consumer for the mailMessages
        List<MailMessage> actualConsumedMailMessages = new ArrayList<>();

        //init and run client in thread
        initClient(EMailClients.createImap(), actualConsumedMailMessages::add);
        runnableEMailClient.login();
        runClientThread();

        //create random mimeMessages TODO test real MimeMessages not only text mimeMessages!
        List<MimeMessage> mimeMessages = new ArrayList<>();
        mimeMessages.add(TestUtil.generateRandomTextMimeMessage(greenMail.getImap().getServerSetup()));
        mimeMessages.add(TestUtil.generateRandomTextMimeMessage(greenMail.getImap().getServerSetup()));
        mimeMessages.add(TestUtil.generateRandomTextMimeMessage(greenMail.getImap().getServerSetup()));
        //deliver messages via greenMail
        mimeMessages.forEach(greenMailUser::deliver);

        //wait 1 second for receiving the messages
        TimeUnit.SECONDS.sleep(1);

        //convert the messages received (and sent) by greenMail to mailMessages
        Message[] receivedMessages = TestUtil.retrieveMessages(greenMail.getImap(), greenMailUser);
        List<MailMessage> expectedConsumedMailMessages = Stream.of(receivedMessages).map(MessageConverter.toMailMessage).collect(Collectors.toList());

        //compare expected MailMessages with actual MailMessages
        Assertions.assertEquals(expectedConsumedMailMessages.size(), actualConsumedMailMessages.size());
        for (int i = 0; i < expectedConsumedMailMessages.size(); i++) {
            Assertions.assertEquals(expectedConsumedMailMessages.get(i), actualConsumedMailMessages.get(i));
        }
    }

}
