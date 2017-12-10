package de.haw.eventalert.ledbridge.entity.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import de.haw.eventalert.ledbridge.entity.color.types.Color;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@JsonTypeName("colorEvent")
public @Data
class ColorEvent extends LEDEvent {

    public ColorEvent() {
        super("colorEvent");
    }

    Color color;
}
