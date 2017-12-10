package de.haw.eventalert.ledbridge.entity.led;

import de.haw.eventalert.ledbridge.entity.led.type.Partable;

/**
 * Created by Tim on 12.05.2017.
 */
public abstract class PartableLed<ColorType> extends DyeableLed<ColorType> implements Partable<ColorType> {
    public PartableLed(ColorType color, int brightness) {
        //super(color, brightness);
    }
}
