package de.haw.eventalert.source.email.entity;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by Tim on 18.08.2017.
 */
public
class MailMessage {
    public static final String EVENT_TYPE = "email";

    private Long emailSourceId;
    private String from;
    private String to;
    private String replyTo;
    private String cc;
    private String bcc;
    private Long sendTime;
    private Long receivedTime;
    private String subject;
    private String content;

    public Long getEmailSourceId() {
        return emailSourceId;
    }

    public MailMessage setEmailSourceId(Long emailSourceId) {
        this.emailSourceId = emailSourceId;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public MailMessage setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getTo() {
        return to;
    }

    public MailMessage setTo(String to) {
        this.to = to;
        return this;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public MailMessage setReplyTo(String replyTo) {
        this.replyTo = replyTo;
        return this;
    }

    public String getCc() {
        return cc;
    }

    public MailMessage setCc(String cc) {
        this.cc = cc;
        return this;
    }

    public String getBcc() {
        return bcc;
    }

    public MailMessage setBcc(String bcc) {
        this.bcc = bcc;
        return this;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public MailMessage setSendTime(Long sendTime) {
        this.sendTime = sendTime;
        return this;
    }

    public Long getReceivedTime() {
        return receivedTime;
    }

    public MailMessage setReceivedTime(Long receivedTime) {
        this.receivedTime = receivedTime;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public MailMessage setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getContent() {
        return content;
    }

    public MailMessage setContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MailMessage that = (MailMessage) o;

        return new EqualsBuilder()
                .append(emailSourceId, that.emailSourceId)
                .append(from, that.from)
                .append(to, that.to)
                .append(replyTo, that.replyTo)
                .append(cc, that.cc)
                .append(bcc, that.bcc)
                .append(sendTime, that.sendTime)
                .append(receivedTime, that.receivedTime)
                .append(subject, that.subject)
                .append(content, that.content)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(emailSourceId)
                .append(from)
                .append(to)
                .append(replyTo)
                .append(cc)
                .append(bcc)
                .append(sendTime)
                .append(receivedTime)
                .append(subject)
                .append(content)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "MailMessage{" +
                "emailSourceId=" + emailSourceId +
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
