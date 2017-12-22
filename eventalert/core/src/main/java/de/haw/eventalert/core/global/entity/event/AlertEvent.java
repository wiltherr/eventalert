package de.haw.eventalert.core.global.entity.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import de.haw.eventalert.core.global.util.Utils;

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
}
