package de.haw.eventalert.ledbridge.entity.led.effect;


import de.haw.eventalert.ledbridge.entity.color.types.Color;
import de.haw.eventalert.ledbridge.entity.led.type.Dimmable;
import de.haw.eventalert.ledbridge.entity.led.type.Dyeable;
import lombok.Data;

/**
 * Created by Tim on 04.05.2017.
 */
public @Data
abstract class TimedColorEffect implements Dimmable, Dyeable<Color>, TimeLimited {
    private Color color;
    private int brightness;
    private long duration;
}
