package de.haw.eventalert.ledbridge.entity.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import de.haw.eventalert.ledbridge.entity.led.type.Dimmable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@JsonTypeName("dimEvent")
public @Data
class DimEvent extends LEDEvent implements Dimmable {
    private int brightness;

    public DimEvent() {
        super("dimEvent");
    }
}
