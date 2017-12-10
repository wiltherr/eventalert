package de.haw.eventalert.ledbridge.entity.led.type;


/**
 * Created by Tim on 12.05.2017.
 */
public interface Dyeable<ColorType> {
    ColorType getColor();

    void setColor(ColorType color);
}
