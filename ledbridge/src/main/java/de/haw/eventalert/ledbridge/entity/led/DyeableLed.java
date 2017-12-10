package de.haw.eventalert.ledbridge.entity.led;


import de.haw.eventalert.ledbridge.entity.led.type.Dyeable;

/**
 * Created by Tim on 12.05.2017.
 */
public abstract class DyeableLed<ColorType> extends DimmableLED implements Dyeable<ColorType> {

    protected ColorType color;

    @Override
    public ColorType getColor() {
        return color;
    }

    @Override
    public void setColor(ColorType color) {
        this.color = color;
    }
}
