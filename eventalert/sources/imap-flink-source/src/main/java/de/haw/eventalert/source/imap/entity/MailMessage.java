package de.haw.eventalert.source.imap.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by Tim on 18.08.2017.
 */
public @Getter
@Setter
@EqualsAndHashCode
class MailMessage {
    public static final String EVENT_TYPE = "email";

    private Long imapAccountId;
    private String from;
    private String to;
    private String replyTo;
    private String cc;
    private String bcc;
    private Long sendTime;
    private Long receivedTime;
    private String subject;
    private String content;

    @Override
    public String toString() {
        return "MailMessage{" +
                "imapAccountId=" + imapAccountId +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", replyTo='" + replyTo + '\'' +
                ", cc='" + cc + '\'' +
                ", bcc='" + bcc + '\'' +
                ", sendTime=" + sendTime +
                ", receivedTime=" + receivedTime +
                ", subject='" + subject + '\'' +
                ", content='" + StringEscapeUtils.escapeJava(content) + '\'' +
                '}';
    }
}
