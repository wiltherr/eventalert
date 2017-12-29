package de.haw.eventalert.source.email.client;

import de.haw.eventalert.source.email.entity.MailMessage;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.util.function.Function;

/**
 * Created by Tim on 29.04.2017.
 * is used to convert {@link Message}
 */
public class MessageConverter {

    /**
     * converts {@link Message} to {@link MailMessage}.
     * <p>empty or missing parameters will be set null.
     */
    public static final Function<Message, MailMessage> toMailMessage = message -> {
        MailMessage mailMessage = new MailMessage();
        //From
        try {
            mailMessage.setFrom(InternetAddress.toString(message.getFrom()));
        } catch (MessagingException e) {
            mailMessage.setFrom(null);
        }
        //To
        try {
            mailMessage.setTo(InternetAddress.toString(message.getRecipients(Message.RecipientType.TO)));
            //TODO The old way:
//                mailMessage.setTo(
//                        javaxAddressToStringList.apply(message.getRecipients(Message.RecipientType.TO))
//                );
        } catch (MessagingException e) {
            mailMessage.setTo(null);
        }
        //ReplyTo
        try {
            mailMessage.setReplyTo(InternetAddress.toString(message.getReplyTo()));
        } catch (MessagingException e) {
            mailMessage.setReplyTo(null);
        }
        //CC
        try {
            mailMessage.setCc(InternetAddress.toString(message.getRecipients(Message.RecipientType.CC)));
        } catch (MessagingException e) {
            mailMessage.setCc(null);
        }
        //BCC
        try {
            mailMessage.setBcc(InternetAddress.toString(message.getRecipients(Message.RecipientType.BCC)));
        } catch (MessagingException e) {
            mailMessage.setBcc(null);
        }

        //SendTime
        try {
            mailMessage.setSendTime(message.getSentDate().getTime());
        } catch (MessagingException e) {
            mailMessage.setSendTime(null);
        }

        //ReceivedTime
        try {
            if (message.getReceivedDate() == null) //POP3 does not support received date
                mailMessage.setReceivedTime(null);
            else
                mailMessage.setReceivedTime(message.getReceivedDate().getTime());
        } catch (MessagingException e) {
            mailMessage.setReceivedTime(null);
        }

        //Subject
        try {
            mailMessage.setSubject(message.getSubject());
        } catch (MessagingException e) {
            mailMessage.setSubject(null);
        }
        //Content
        try {
            //message.getContentType();
            //message.isMimeType(???)
            //TODO check mimeType or Multipart shit..
            mailMessage.setContent(message.getContent().toString());
        } catch (IOException | MessagingException e) {
            mailMessage.setContent(null);
        }

        return mailMessage;
    };

    /**
     * constructor is private because all functions should be declared as static final variable
     */
    private MessageConverter() {
    }

}

//TODO the old way
//    private static Function<Address[],List<String>> javaxAddressToStringList = new Function<Address[], List<String>>() {
//        @Override
//        public List<String> apply(Address[] addresses) {
//            List<String> resultList = new ArrayList<String>();
//            for(Address address : addresses) {
//                resultList.add(address.toString());
//            }
//            return resultList;
//        }
//    };

