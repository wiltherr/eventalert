package de.haw.eventalert.source.email.test;

import com.icegreen.greenmail.server.AbstractServer;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.Retriever;
import com.icegreen.greenmail.util.ServerSetup;
import de.haw.eventalert.source.email.client.MessageConverter;
import de.haw.eventalert.source.email.configuration.EMailSourceConfiguration;
import de.haw.eventalert.source.email.entity.MailMessage;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestUtil {
    public static MimeMessage generateRandomTextMimeMessage(ServerSetup serverSetup) {
        String receiverAddress = generateRandomAddress();
        String senderAddress = generateRandomAddress();
        String subject = GreenMailUtil.random(25);
        String content = GreenMailUtil.random(200);
        return GreenMailUtil.createTextEmail(receiverAddress, senderAddress, subject, content, serverSetup);
    }

    public static String generateRandomAddress() {
        String firstName = GreenMailUtil.random();
        String lastName = GreenMailUtil.random();
        String mail = GreenMailUtil.random();
        String domain = GreenMailUtil.random();
        return firstName + " " + lastName + " <" + mail + "@" + domain + ".de" + ">";
    }

    public static List<MailMessage> retrieveAsMailMessageList(AbstractServer server, GreenMailUser toUser) {
        return Stream.of(retrieveMessages(server, toUser)).map(MessageConverter.toMailMessage).collect(Collectors.toList());
    }

    public static Message[] retrieveMessages(AbstractServer server, GreenMailUser toUser) {
        Retriever retriever = new Retriever(server);
        return retriever.getMessages(toUser.getLogin(), toUser.getPassword());
    }

    public static void deliverRandomTextMessages(AbstractServer server, GreenMailUser user, int messageCount) {
        for (int i = 0; i < messageCount; i++) {
            deliverRandomTextMessage(server, user);
        }
        //TODO add real html MimeMessages
    }

    public static void deliverRandomTextMessagesWithDelay(AbstractServer server, GreenMailUser user, int messageCount, int delayInMs) throws InterruptedException {
        for (int i = 0; i < messageCount; i++) {
            deliverRandomTextMessage(server, user);
            TimeUnit.MILLISECONDS.sleep(delayInMs);
        }
        //TODO add real html MimeMessages
    }

    private static void deliverRandomTextMessage(AbstractServer server, GreenMailUser user) {
        user.deliver(TestUtil.generateRandomTextMimeMessage(server.getServerSetup()));
    }

    public static Properties generateEMailSourceProperties(AbstractServer server, GreenMailUser user) {
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




}
