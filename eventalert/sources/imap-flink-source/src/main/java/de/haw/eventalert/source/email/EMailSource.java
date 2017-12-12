package de.haw.eventalert.source.email;


import de.haw.eventalert.source.email.client.EMailClient;
import de.haw.eventalert.source.email.client.imap.EMailImapClient;
import de.haw.eventalert.source.email.configuration.EMailSourceConfiguration;
import de.haw.eventalert.source.email.entity.MailMessage;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Tim on 18.08.2017.
 */
public class EMailSource implements SourceFunction<MailMessage> {
    private static final Logger LOG = LoggerFactory.getLogger(EMailSource.class);
    private volatile boolean isRunning = true;

    private EMailClient client;
    private Long sourceId;

    public EMailSource(Properties eMailSourceConfProperties) throws IOException {
        this(EMailSourceConfiguration.fromProperties(eMailSourceConfProperties));
    }

    public EMailSource(EMailSourceConfiguration sourceConfiguration) {
        this.client = new EMailImapClient();
        //TODO protocol in die property, vielleicht auch einfach die javax properties übernhmen?
        this.client.init("imaps", sourceConfiguration.getHost(), sourceConfiguration.getPort(), sourceConfiguration.getUser(), sourceConfiguration.getPassword(), sourceConfiguration.getFolder());
        this.sourceId = sourceConfiguration.getId();
    }

    @Override
    public void run(SourceContext<MailMessage> sourceContext) throws Exception {
        //set the collect function as consumer
        client.setConsumer(sourceContext::collect);
        client.login();
        client.runClient();
    }

    @Override
    public void cancel() {
        client.cancel();
    }

//    private Stream<Message> getNewMessages() throws IOException {
//        client.
//    }
//TODO message als stream?!
}
