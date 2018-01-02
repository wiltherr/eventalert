package de.haw.eventalert.core.producer;

import de.haw.eventalert.core.global.EventAlertConst;
import de.haw.eventalert.core.global.alertevent.AlertEvent;
import de.haw.eventalert.core.global.alertevent.AlertEvents;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

import java.util.Properties;

/**
 * Util class for producing {@link AlertEvent} streams to Kafka
 */
public class AlertEventProducer {

    private static final String KAFKA_BROKER = EventAlertConst.KAFA_BROKER;
    private static final String KAFKA_TOPIC = EventAlertConst.KAFKA_TOPIC_ALERTEVENT;

    /**
     * creates a kafka producer that emits all {@link AlertEvent}s to EventAlert-topic
     * <p>this method is used to provide a alertEventStream to EventAlert
     *
     * @param alertEventStream stream of {@link AlertEvent}s
     * @see de.haw.eventalert.core.producer.example.ExampleProducerJob for example usage
     */
    public static void createAlertEventProducer(DataStream<AlertEvent> alertEventStream) {
        createAlertEventJsonProducer(alertEventStream.flatMap(
                AlertEvents.convertToJSONString()
        ));
    }

    private static void createAlertEventJsonProducer(DataStream<String> alertEventJSONStreamSource) {
        Properties producerProperties = new Properties();
        producerProperties.setProperty("bootstrap.servers", KAFKA_BROKER);

        // add source to kafka producer
        FlinkKafkaProducer010.FlinkKafkaProducer010Configuration<String> flinkKafkaProducer010 = FlinkKafkaProducer010.writeToKafkaWithTimestamps(
                alertEventJSONStreamSource,
                KAFKA_TOPIC,
                new SimpleStringSchema(),
                producerProperties
        );

        flinkKafkaProducer010.setLogFailuresOnly(false);
        flinkKafkaProducer010.setFlushOnCheckpoint(true);
    }


}
