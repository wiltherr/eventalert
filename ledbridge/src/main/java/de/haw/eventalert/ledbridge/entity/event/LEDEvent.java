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
        @JsonSubTypes.Type(value = ColorLEDEvent.class, name = "color")
})
public @Data
class LEDEvent implements Serializable {
    private String type;
    private long targetLEDId;
}
