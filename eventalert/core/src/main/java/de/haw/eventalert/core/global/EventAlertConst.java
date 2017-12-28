package de.haw.eventalert.core.global;

/**
 * constants for EventAlert
 */
public class EventAlertConst {
    /**
     * address / ip of Kafka broker
     */
    public static final String KAFA_BROKER = "localhost:9092";
    /**
     * kafka topic which is used by {@link de.haw.eventalert.core.producer.AlertEventProducer} and {@link de.haw.eventalert.core.consumer.AlertEventConsumer}
     */
    public static final String KAFKA_TOPIC_ALERTEVENT = "alert-events";
}
