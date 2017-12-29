package de.haw.eventalert.core.producer.telegram;

import de.haw.eventalert.core.global.alertevent.AlertEvent;
import de.haw.eventalert.core.global.alertevent.AlertEvents;
import de.haw.eventalert.core.producer.AlertEventProducer;
import de.haw.eventalert.source.telegram.TelegramSource;
import de.haw.eventalert.source.telegram.client.TelegramMessageEvent;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelegramAlertEventProducerJob {
    private static final Logger LOG = LoggerFactory.getLogger(TelegramAlertEventProducerJob.class);

    public static void main(String[] args) throws Exception {
        LOG.info("========== TelegramAlertEventProducerJob started ==========");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        DataStream<TelegramMessageEvent> messageDataStream = env.addSource(new TelegramSource());
        DataStream<AlertEvent> alertEventDataStream = messageDataStream.flatMap(((tgEvent, out) -> {
            try {
                AlertEvent event = AlertEvents.createEvent(TelegramMessageEvent.EVENT_TYPE, tgEvent);
                out.collect(event);
                LOG.debug("New alertEvent emitted: {}", event);
            } catch (Exception e) {
                LOG.error("Can not convert event: {}", tgEvent, e);
            }
        }));

        AlertEventProducer.createAlertEventProducer(alertEventDataStream);
        env.execute("TelegramAlertEventProducerJob");
    }
}
