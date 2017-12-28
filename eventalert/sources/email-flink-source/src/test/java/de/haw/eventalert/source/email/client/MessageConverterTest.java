package de.haw.eventalert.source.email.client;

import com.icegreen.greenmail.server.AbstractServer;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.util.ServerSetupTest;
import de.haw.eventalert.source.email.entity.MailMessage;
import de.haw.eventalert.source.email.test.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;

public class MessageConverterTest {
    private GreenMail greenMail;
    private GreenMailUser greenMailUser;

    private static void testConvertTextMessages(AbstractServer server, GreenMailUser user) throws IOException, MessagingException {
        //deliver random messages with greenMail
        TestUtil.deliverRandomTextMessages(server, user, 5);
        //retrieved the messages, that were delivered before
        Message[] messages = TestUtil.retrieveMessages(server, user);

        //convert and check messages
        for (Message message : messages) {
            //convert to MailMessage
            MailMessage convertedMessage = MessageConverter.toMailMessage.apply(message);
            //check that all parameters match
            assertMatch(message, convertedMessage);
        }
    }

    /**
     * assert that fields values of {@link Message} equals to all available fields of {@link MailMessage}
     *
     * @param message     that was converted to {@link MailMessage} before
     * @param mailMessage converted target {@link MailMessage}
     * @throws MessagingException will be thrown if any getter of {@link Message} fails
     * @throws IOException        will be thrown if the {@link Message#getContent()} fails
     */
    private static void assertMatch(Message message, MailMessage mailMessage) throws MessagingException, IOException {
        //addresses equals
        Assertions.assertEquals(InternetAddress.toString(message.getFrom()), mailMessage.getFrom(), "field from not match");
        Assertions.assertEquals(InternetAddress.toString(message.getRecipients(Message.RecipientType.TO)), mailMessage.getTo(), "field to not match");
        Assertions.assertEquals(InternetAddress.toString(message.getRecipients(Message.RecipientType.CC)), mailMessage.getCc(), "field cc not match");
        Assertions.assertEquals(InternetAddress.toString(message.getRecipients(Message.RecipientType.BCC)), mailMessage.getBcc(), "field bcc not match");
        Assertions.assertEquals(InternetAddress.toString(message.getReplyTo()), mailMessage.getReplyTo(), "field replyTo not match");

        //timing equals
        Assertions.assertEquals(message.getSentDate().getTime(), (long) mailMessage.getSendTime(), "field sentTime not match");
        if (message.getReceivedDate() == null) //on POP3 received date is not supported
            Assertions.assertNull(mailMessage.getReceivedTime(), "field receivedTime not match");
        else
            Assertions.assertEquals(message.getReceivedDate().getTime(), (long) mailMessage.getReceivedTime(), "field receivedTime not match");

        //content equals
        Assertions.assertEquals(message.getSubject(), mailMessage.getSubject(), "subject not match");
        Assertions.assertEquals(message.getContent().toString(), mailMessage.getContent(), "message content not match");
    }

    @AfterEach
    private void tearDown() {
        if (greenMail != null)
            greenMail.stop();
    }

    private void initGreenMail(ServerSetup serverSetup) {
        if (greenMail != null)
            tearDown();
        greenMail = new GreenMail(serverSetup);
        greenMailUser = greenMail.setUser(GreenMailUtil.random(), GreenMailUtil.random());
        greenMail.start();
    }

    @Test
    public void testConvertImap() throws IOException, MessagingException {
        initGreenMail(ServerSetupTest.IMAP);
        testConvertTextMessages(greenMail.getImap(), greenMailUser);
    }

    @Test
    public void testConvertPop3() throws IOException, MessagingException {
        initGreenMail(ServerSetupTest.POP3);
        testConvertTextMessages(greenMail.getPop3(), greenMailUser);
    }
}
