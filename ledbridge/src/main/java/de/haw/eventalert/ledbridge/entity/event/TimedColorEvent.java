package de.haw.eventalert.ledbridge.entity.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import de.haw.eventalert.ledbridge.entity.color.types.Color;
import de.haw.eventalert.ledbridge.entity.led.effect.TimeLimited;
import de.haw.eventalert.ledbridge.entity.led.type.Dimmable;
import de.haw.eventalert.ledbridge.entity.led.type.Dyeable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@JsonTypeName("timedColorEvent")
public @Data
class TimedColorEvent extends LEDEvent implements Dimmable, Dyeable<Color>, TimeLimited {
    private Color color;
    private int brightness;
    private long duration;

    public TimedColorEvent() {
        super("timedColorEvent");
    }
}