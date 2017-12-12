package de.haw.eventalert.source.email.client;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.util.ServerSetupTest;
import de.haw.eventalert.source.email.client.imap.EMailImapClient;
import de.haw.eventalert.source.email.client.exception.EMailSourceClientExecutionException;
import de.haw.eventalert.source.email.client.exception.EMailSourceClientLoginFailedException;
import de.haw.eventalert.source.email.entity.MailMessage;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static javafx.scene.input.KeyCode.G;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EMailClientTest {

    private static final String TEST_HOST = "";
    private static final String TEST_PORT = "";
    private static final String TEST_USER = "";


    public GreenMail greenMail;
    private GreenMailUser greenMailUser;
    private RunnableEMailClient runnableEMailClient;

    @BeforeEach void setUp() {
        greenMail = new GreenMail(ServerSetupTest.SMTP_IMAP);
        greenMailUser = greenMail.setUser("test@email.com",GreenMailUtil.random(), GreenMailUtil.random());
        greenMail.start();
    }

    @AfterEach void tearDown() {
        runnableEMailClient.cancel();
        greenMail.stop();
    }

    private void initClient(EMailClient eMailClient, Consumer<MailMessage> mailMessageConsumer) {
        runnableEMailClient = new RunnableEMailClient(eMailClient);
        String host = greenMail.getImap().getServerSetup().getBindAddress();
        int port = greenMail.getImap().getServerSetup().getPort();
        String folderName = "INBOX";
        String user = greenMailUser.getLogin();
        String password = greenMailUser.getPassword();
        runnableEMailClient.init("imap",host,port,user,password,folderName);
        runnableEMailClient.setConsumer(mailMessageConsumer);
    }

    private void runClientThread() {
        Thread thread = new Thread(runnableEMailClient);
        thread.start();
    }

    @Test
    public void testImap() throws EMailSourceClientExecutionException, EMailSourceClientLoginFailedException, InterruptedException {
        List<MailMessage> receivedMailMessages = new ArrayList<>();
        initClient(EMailClients.createImap(), receivedMailMessages::add);
        runnableEMailClient.login();

        runClientThread();
        //MimeMessage mimeMessage = GreenMailUtil.createTextEmail("Receiver Test <test@receiver.de>","Sender Test <test@sender.de>", "Test Subject", "Test Messsage",greenMail.getImap().getServerSetup());
        //MimeMessage mimeMessageText = GreenMailUtil.createTextEmail("test@receiver.de","test@sender.de", "Test Subject", "Test Messsage",greenMail.getImap().getServerSetup());
        GreenMailUtil.sendTextEmailTest("Receiver Test <test@receiver.de>","Sender Test <test@sender.de>", "Test Subject", "Test Messsage");

        //greenMailUser.deliver(mimeMessageText);

        Thread.sleep(30000);

        runnableEMailClient.cancel();

        receivedMailMessages.forEach(e -> System.out.println(e.getContent()));
        assertTrue(receivedMailMessages.size() == 1);
        //EMailImapClient.login();
        //EMailImapClient.run();
    }

    private void initClient() {

    }

}
