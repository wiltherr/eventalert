package de.haw.eventalert.core.producer.telegram;

import de.haw.eventalert.core.global.alertevent.AlertEvent;
import de.haw.eventalert.core.global.alertevent.AlertEvents;
import de.haw.eventalert.core.producer.AlertEventProducer;
import de.haw.eventalert.source.telegram.TelegramSource;
import de.haw.eventalert.source.telegram.api.auth.TelegramAuthentication;
import de.haw.eventalert.source.telegram.api.auth.util.TelegramAuthenticationFileUtil;
import de.haw.eventalert.source.telegram.client.TelegramMessageEvent;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;

public class TelegramAlertEventProducerJob {
    private static final Logger LOG = LoggerFactory.getLogger(TelegramAlertEventProducerJob.class);

    public static void main(String[] args) throws Exception {
        TelegramSource telegramSource = null;
        if (args.length == 0) { //TODO disable in production mode
            //use default auth storage
            telegramSource = new TelegramSource();
        } else if (args.length < 2) {
            System.err.println("Error: A telegram authentication file is needed! Add file path as second parameter!");
            System.exit(1);
        } else {
            try {
                TelegramAuthentication authentication = TelegramAuthenticationFileUtil.readFromFile(Paths.get(args[1]));
                telegramSource = new TelegramSource(authentication);
            } catch (IOException e) {
                System.err.println("Error: Configuration file not found: " + e.getMessage());
                System.exit(1);
            }
        }

        LOG.info("========== TelegramAlertEventProducerJob started ==========");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        DataStream<TelegramMessageEvent> messageDataStream = env.addSource(telegramSource);
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
