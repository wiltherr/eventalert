package de.haw.eventalert.source.email.client;

import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import de.haw.eventalert.source.email.client.test.TestUtil;
import de.haw.eventalert.source.email.entity.MailMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;

public class MessageConverterTest {
    private GreenMail greenMail;
    private GreenMailUser greenMailUser;

    private void initGreenMail(ServerSetup serverSetup) {
        greenMail = new GreenMail(serverSetup);
        greenMailUser = greenMail.setUser(GreenMailUtil.random(), GreenMailUtil.random());
        greenMail.start();
    }

    @Test
    public void testConvertTextMimeMessages() throws IOException, MessagingException {
        //send random messages with greenMail to later get the received messages
        greenMailUser.deliver(TestUtil.generateRandomTextMimeMessage(greenMail.getImap().getServerSetup()));
        greenMailUser.deliver(TestUtil.generateRandomTextMimeMessage(greenMail.getImap().getServerSetup()));
        greenMailUser.deliver(TestUtil.generateRandomTextMimeMessage(greenMail.getImap().getServerSetup()));

        //retrieved the received messages, that were sent before
        Message[] sentMessages = TestUtil.retrieveMessages(greenMail.getImap(), greenMailUser);

        //convert and check messages
        for (Message message : sentMessages) {
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
    private void assertMatch(Message message, MailMessage mailMessage) throws MessagingException, IOException {
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
}
