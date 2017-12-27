package de.haw.eventalert.core.global.entity.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import de.haw.eventalert.core.global.util.Utils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by Tim on 19.08.2017.
 */
public class AlertEvent implements Serializable {
    private String eventType;

    private JsonNode eventData;

    @SuppressWarnings("unused") //jackson needs default constructor
    public AlertEvent() {
    }


    public AlertEvent(String eventType, Object eventObj) throws JsonProcessingException {
        this.eventType = eventType;
        this.eventData = Utils.jsonMapper.valueToTree(eventObj);
    }

    public String getEventType() {
        return eventType;
    }

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
