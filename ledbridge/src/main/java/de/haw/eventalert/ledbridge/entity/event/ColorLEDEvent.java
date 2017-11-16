package de.haw.eventalert.ledbridge.entity.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import de.haw.eventalert.ledbridge.entity.color.types.Color;
import lombok.Data;

@JsonTypeName("color")
public @Data
class ColorLEDEvent extends LEDEvent {
    Color color;
}
