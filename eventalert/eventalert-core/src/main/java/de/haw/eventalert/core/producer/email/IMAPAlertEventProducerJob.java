package de.haw.eventalert.core.producer.email;

import de.haw.eventalert.core.global.AlertEvents;
import de.haw.eventalert.core.global.entity.event.AlertEvent;
import de.haw.eventalert.core.producer.EventAlertProducer;
import de.haw.eventalert.source.imap.ImapSource;
import de.haw.eventalert.source.imap.entity.MailMessage;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Tim on 18.08.2017.
 */
public class IMAPAlertEventProducerJob {
    private static final Logger LOG = LoggerFactory.getLogger(IMAPAlertEventProducerJob.class);

    public static void main(String[] args) throws Exception {
        LOG.info("========== IMAPAlertEventProducerJob started ==========");

        //get the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        //create the desired source
        ImapSource imapSource = new ImapSource("timATksit.org.properties");
        //add source to environment
        DataStream<MailMessage> mailMessageSource = env.addSource(imapSource);
        //convert source events to alertEvents
        DataStream<AlertEvent> alertEventDataStream = mailMessageSource.flatMap((mailMessage, out) -> {
            try {
                out.collect(AlertEvents.createEvent(MailMessage.EVENT_TYPE, mailMessage));
            } catch (Exception e) {
                LOG.error("Error creating alertEvent out of mailMessage({})", mailMessage, e);
            }
        });

        //Add stream to kafka
        EventAlertProducer.provideDataStream(alertEventDataStream);

        env.execute("IMAPAlertEventProducerJob");
        env.getConfig().enableObjectReuse();
    }
}
