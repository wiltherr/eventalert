package de.haw.eventalert.source.email.client.test;

import com.icegreen.greenmail.server.AbstractServer;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.Retriever;
import com.icegreen.greenmail.util.ServerSetup;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

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

    public static Message[] retrieveMessages(AbstractServer server, GreenMailUser toUser) {
        Retriever retriever = new Retriever(server);
        return retriever.getMessages(toUser.getLogin(), toUser.getPassword());
    }

    public static void deliverRandomTextMessages(AbstractServer server, GreenMailUser user, int messageCount) {
        for (int i = 0; i <= messageCount; i++) {
            user.deliver(TestUtil.generateRandomTextMimeMessage(server.getServerSetup()));
        }
        //TODO add real html MimeMessages
    }


}
