package de.haw.eventalert.ledbridge.entity.event;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DimEvent.class, name = "dimEvent"),
        @JsonSubTypes.Type(value = ColorEvent.class, name = "colorEvent"),
        @JsonSubTypes.Type(value = TimedColorEvent.class, name = "timedColorEvent")
})
public abstract @Data
class LEDEvent implements Serializable {

    public LEDEvent(String type) {
        this.type = type;
    }

    private String type;
    private long targetLEDId;
}

