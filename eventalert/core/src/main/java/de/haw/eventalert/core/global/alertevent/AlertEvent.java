package de.haw.eventalert.core.global.alertevent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import de.haw.eventalert.core.global.util.Utils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * data object for communication between {@link de.haw.eventalert.core.producer.AlertEventProducer} and {@link de.haw.eventalert.core.consumer.AlertEventConsumer}
 */
public class AlertEvent implements Serializable {
    private String eventType;
    private JsonNode eventData;

    @SuppressWarnings("unused")
    AlertEvent() {
        //jackson needs default constructor
    }

    /**
     * constructor to create a new alertEvent out of any POJO.
     * use factory method {@link AlertEvents#createEvent(String, Object)} instead of constructor!
     *
     * @see AlertEvents#createEvent(String, Object)
     */
    AlertEvent(String eventType, Object eventObj) throws JsonProcessingException {
        this.eventType = eventType;
        this.eventData = Utils.jsonMapper.valueToTree(eventObj);
    }

    /**
     * gets the original event type name
     *
     * @return original event type name
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * gets the original event data as {@link JsonNode}
     *
     * @return original event data as {@link JsonNode}
     */
    public JsonNode getEventData() {
        return eventData;
    }

    @Override
    public String toString() {
        return "AlertEvent{" +
                "eventType='" + eventType + '\'' +
                ", eventData=" + eventData +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AlertEvent that = (AlertEvent) o;

        return new EqualsBuilder()
                .append(eventType, that.eventType)
                .append(eventData, that.eventData)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(eventType)
                .append(eventData)
                .toHashCode();
    }
}
