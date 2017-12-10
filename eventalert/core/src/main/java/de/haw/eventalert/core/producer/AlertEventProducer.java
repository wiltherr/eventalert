package de.haw.eventalert.core.producer;

import de.haw.eventalert.core.global.AlertEvents;
import de.haw.eventalert.core.global.EventAlertConst;
import de.haw.eventalert.core.global.entity.event.AlertEvent;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by Tim on 02.09.2017.
 */
public class AlertEventProducer {

    public static final Logger LOG = LoggerFactory.getLogger(AlertEventProducer.class);

    private static final String KAFA_BROKER = EventAlertConst.KAFA_BROKER;
    private static final String KAFKA_TOPIC = EventAlertConst.KAFKA_TOPIC_ALERTEVENT;

    public static void createAlertEventProducer(DataStream<AlertEvent> alertEventStreamSource) {
        createAlertEventJsonProducer(alertEventStreamSource.flatMap(
                AlertEvents.convertToJSONString()
        ));
    }

    private static void createAlertEventJsonProducer(DataStream<String> alertEventJSONStreamSource) {
        Properties producerProperties = new Properties(); //TODO settings should can be overwritten by calling class
        producerProperties.setProperty("bootstrap.servers", KAFA_BROKER);

        // add source to kafka producer
        FlinkKafkaProducer010.FlinkKafkaProducer010Configuration<String> flinkKafkaProducer010 = FlinkKafkaProducer010.writeToKafkaWithTimestamps(
                alertEventJSONStreamSource,
                KAFKA_TOPIC,
                new SimpleStringSchema(),
                producerProperties
        );

        // the following is necessary for at-least-once delivery guarantee TODO: was macht das?
        flinkKafkaProducer010.setLogFailuresOnly(false);   // "false" by default
        flinkKafkaProducer010.setFlushOnCheckpoint(true);  // "false" by default
    }


}
