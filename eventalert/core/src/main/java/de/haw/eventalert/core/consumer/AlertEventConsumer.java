package de.haw.eventalert.core.consumer;

import de.haw.eventalert.core.global.EventAlertConst;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.UUID;

/**
 * Created by Tim on 04.09.2017.
 */
public class AlertEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(AlertEventConsumer.class);

    private static final String KAFKA_BROKER = EventAlertConst.KAFA_BROKER;
    private static final String KAFKA_TOPIC = EventAlertConst.KAFKA_TOPIC_ALERTEVENT;

    public static FlinkKafkaConsumer010<String> createAlertEventConsumer() {
        // set up kafka consumer
        Properties properties = new Properties(); //TODO settings should can be overwritten by calling class
        properties.setProperty("auto.offset.reset", "latest");
//        properties.setProperty("auto.offset.reset", "earliest");
        properties.setProperty("bootstrap.servers", KAFKA_BROKER);
        // random consumer group id
        properties.setProperty("group.id", UUID.randomUUID().toString());

        return new FlinkKafkaConsumer010<>(KAFKA_TOPIC, new SimpleStringSchema(), properties);
    }


}
