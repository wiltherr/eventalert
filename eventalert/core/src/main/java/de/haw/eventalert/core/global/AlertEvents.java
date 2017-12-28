package de.haw.eventalert.core.global;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.haw.eventalert.core.global.entity.event.AlertEvent;
import de.haw.eventalert.core.global.util.Utils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Util class for creating and converting AlertEvents.
 * Is used by AlertEventConsumer- and -ProducerJobs.
 *
 * @see de.haw.eventalert.core.producer.example.ExampleProducerJob
 * @see de.haw.eventalert.core.consumer.AlertEventConsumerJob
 */
public class AlertEvents {

    private static final Logger LOG = LoggerFactory.getLogger(AlertEvents.class);

    /**
     * creates a new alertEvent of any POJO object
     * <p>
     * note: the original object will be converted to json.
     * the {@link com.fasterxml.jackson.databind.ObjectMapper} will need the original object to implement
     * a default constructor and getters/setters for all fields
     *
     * @param eventType   name of original (nested) event
     * @param eventObject original event object (will be converted to json)
     * @return a new AlertEvent
     * @throws JsonProcessingException can be thrown if the passed eventObject is not valid for converting to json
     */
    public static AlertEvent createEvent(String eventType, Object eventObject) throws JsonProcessingException {
        return new AlertEvent(eventType, eventObject);
    }

    /**
     * converts a AlertEvent to a json string
     *
     * @param alertEvent the alertEvent that should be converted
     * @return the json representation of AlertEvent
     * @throws JsonProcessingException can be thrown if alertEvent can not be converted
     */
    public static String toJSONString(AlertEvent alertEvent) throws JsonProcessingException {
        return Utils.jsonMapper.writeValueAsString(alertEvent);
    }

    /**
     * converts a json alertEvent to {@link AlertEvent} object
     *
     * @param jsonAlertEvent json representation of alertEvent
     * @return AlertEvent object
     * @throws IOException can be thrown if the jsonAlertEvent string is not valid
     */
    public static AlertEvent toAlertEvent(String jsonAlertEvent) throws IOException {
        return Utils.jsonMapper.readValue(jsonAlertEvent, AlertEvent.class);
    }

    /**
     * {@link FlatMapFunction} that converts a stream of {@link AlertEvent} to its json representation
     *
     * @return a stream of json representations as string
     * @see #toJSONString(AlertEvent)
     */
    public static FlatMapFunction<AlertEvent, String> convertToJSONString() {
        return (alertEvent, out) -> {
            try {
                out.collect(toJSONString(alertEvent));
            } catch (JsonProcessingException e) {
                LOG.error("Could not convert alertEvent({}) to jsonAlertEvent", alertEvent, e);
            }
        };
    }

    /**
     * {@link FlatMapFunction} that converts a stream of alertEvent json strings to its object representation
     *
     * @return a stream of alertEvent objects
     * @see #toAlertEvent(String)
     */
    public static FlatMapFunction<String, AlertEvent> convertToAlertEvent() {
        return (jsonAlertEvent, out) -> {
            try {
                out.collect(toAlertEvent(jsonAlertEvent));
            } catch (IOException e) {
                LOG.error("Could not convert jsonAlertEvent({}) to alertEvent", jsonAlertEvent, e);
            }
        };
    }
}
