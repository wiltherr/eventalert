package de.haw.eventalert.core.producer.email;

import de.haw.eventalert.core.global.alertevent.AlertEvent;
import de.haw.eventalert.core.global.alertevent.AlertEvents;
import de.haw.eventalert.core.producer.AlertEventProducer;
import de.haw.eventalert.source.email.EMailSource;
import de.haw.eventalert.source.email.entity.MailMessage;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


/**
 * Created by Tim on 18.08.2017.
 */
public class IMAPAlertEventProducerJob {
    private static final Logger LOG = LoggerFactory.getLogger(IMAPAlertEventProducerJob.class);

    public static void main(String[] args) throws Exception {
        LOG.info("========== IMAPAlertEventProducerJob started ==========");
        //load the source configuration
        Properties sourceConf = new Properties();
        sourceConf.load(IMAPAlertEventProducerJob.class.getClassLoader().getResourceAsStream("timATksit.org.properties"));
        //get the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        //create the desired source
        EMailSource eMailSource = new EMailSource(sourceConf);
        //add source to environment
        DataStream<MailMessage> mailMessageSource = env.addSource(eMailSource);
        //convert source events to alertEvents
        DataStream<AlertEvent> alertEventDataStream = mailMessageSource.flatMap((mailMessage, out) -> {
            try {
                AlertEvent event = AlertEvents.createEvent(MailMessage.EVENT_TYPE, mailMessage);
                out.collect(event);
                LOG.debug("New alertEvent emitted: {}", event);
            } catch (Exception e) {
                LOG.error("Error creating alertEvent out of mailMessage({})", mailMessage, e);
            }
        });

        //Add stream to kafka
        AlertEventProducer.createAlertEventProducer(alertEventDataStream);

        env.execute("IMAPAlertEventProducerJob");
        env.getConfig().enableObjectReuse();
    }
}
