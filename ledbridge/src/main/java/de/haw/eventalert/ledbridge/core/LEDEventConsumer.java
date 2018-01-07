package de.haw.eventalert.ledbridge.core;


import de.haw.eventalert.ledbridge.entity.event.LEDEvent;
import de.haw.eventalert.ledbridge.entity.event.LEDEventConverter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class LEDEventConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(LEDEventConsumer.class);

    private static final String KAFKA_BROKER = "localhost:9092"; //TODO broker has to be the same as in LEDEventProducer!
    private static final String KAFKA_TOPIC = "led-events"; //TODO topic has to be the same as in LEDEventProducer!
    private final KafkaConsumer<Long, String> consumer;
    private final LEDEventListener listener;
    private boolean isStop = false;
    private long consumerTimeout = 100;

    public LEDEventConsumer(LEDEventListener listener) {
        Properties props = new Properties(); //TODO open kafka consumer properties with producer properties in eventalert.core.ledevent.LEDEventProducer!
        props.put("bootstrap.servers", KAFKA_BROKER);
        // random consumer group id
        props.put("group.id", UUID.randomUUID().toString());
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "latest");  //always get the latest events
        props.put("key.deserializer", "org.apache.kafka.common.serialization.LongDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(KAFKA_TOPIC));

        this.listener = listener;
    }

    public void run() {
        LOG.info("Consumer is started");
        while (!isStop) {
            ConsumerRecords<Long, String> records = consumer.poll(consumerTimeout);
            LOG.trace("Consumer received {} records in last {}ms", records.count(), consumerTimeout);
            for (ConsumerRecord<Long, String> record : records) {
                try {
                    LEDEvent ledEvent = LEDEventConverter.toLEDEvent(record.value());
                    listener.onLEDEvent(ledEvent);
                } catch (Exception e) {
                    LOG.error("error converting ledEvent #{} to object", record.key(), e);
                }
            }

        }
        LOG.info("Consumer is finished / was stopped");
    }

    public void stop() {
        LOG.info("Consumer has been stopped");
        this.isStop = true;
    }

    public interface LEDEventListener {
        void onLEDEvent(LEDEvent ledEvent);
    }
}
