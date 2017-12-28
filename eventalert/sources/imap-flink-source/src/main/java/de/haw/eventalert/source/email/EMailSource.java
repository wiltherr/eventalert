package de.haw.eventalert.source.email;


import de.haw.eventalert.source.email.client.EMailClient;
import de.haw.eventalert.source.email.client.EMailClients;
import de.haw.eventalert.source.email.configuration.EMailSourceConfiguration;
import de.haw.eventalert.source.email.entity.MailMessage;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by Tim on 18.08.2017.
 */
public class EMailSource implements SourceFunction<MailMessage> {
    private static final Logger LOG = LoggerFactory.getLogger(EMailSource.class);

    private EMailClient client;

    public EMailSource(Properties eMailSourceConfProperties) {
        this(EMailSourceConfiguration.fromProperties(eMailSourceConfProperties));
    }

    public EMailSource(EMailSourceConfiguration sourceConfiguration) {
        this.client = EMailClients.createImap();
        this.client.setConfiguration(sourceConfiguration);
    }

    @Override
    public void run(SourceContext<MailMessage> sourceContext) throws Exception {
        //set the collect function as consumer
        client.setConsumer(sourceContext::collect);
        client.runClient();
    }

    @Override
    public void cancel() {
        client.cancel();
    }
}
