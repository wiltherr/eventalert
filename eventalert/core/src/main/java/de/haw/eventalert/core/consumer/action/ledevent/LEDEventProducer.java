package de.haw.eventalert.core.consumer.action.ledevent;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.haw.eventalert.core.global.EventAlertConst;
import de.haw.eventalert.ledbridge.entity.event.LEDEvent;
import de.haw.eventalert.ledbridge.entity.event.LEDEventConverter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class is used for producing {@link LEDEvent}s to Kafka.
 * {@link LEDEvent}s will be executed by the ledbridge ({@link de.haw.eventalert.ledbridge.core.LEDManager})
 */
class LEDEventProducer {
    private static final Logger LOG = LoggerFactory.getLogger(LEDEventProducer.class);
    private static final String KAFKA_BROKER = EventAlertConst.KAFA_BROKER; //TODO make kafka broker configurable (has to be the same as in LEDEventConsumer)
    private static final String KAFKA_TOPIC = "led-events";  //TODO has to be the same as in LEDEventConsumer

    private static LEDEventProducer instance;
    private Producer<Long, String> producer;
    private AtomicLong counter;

    private LEDEventProducer() {
        Properties props = new Properties(); //TODO open kafka producer properties with consumer properties in eventalert.ledbridge.core.LEDEventConsumer!
        props.put("bootstrap.servers", KAFKA_BROKER);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.LongSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        this.producer = new KafkaProducer<>(props);
        this.counter = new AtomicLong(0);
    }

    static LEDEventProducer getInstance() {
        if (instance == null) {
            instance = new LEDEventProducer();
        }
        return instance;
    }

    /**
     * will be executed by {@link LEDEventAction} when the action is executed
     *
     * @param ledEvent a {@link LEDEvent}
     */
    public void emit(LEDEvent ledEvent) {
        try {
            String jsonString = LEDEventConverter.toJsonString(ledEvent);
            producer.send(new ProducerRecord<>(KAFKA_TOPIC, counter.getAndIncrement(), jsonString));
            LOG.debug("Emitted new ledEvent #{}", counter.get());
        } catch (JsonProcessingException e) {
            LOG.error("error converting ledEvent {}", ledEvent, e);
        }
    }
}
