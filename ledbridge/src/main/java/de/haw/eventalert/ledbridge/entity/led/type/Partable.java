package de.haw.eventalert.ledbridge.entity.led.type;

/**
 * Created by Tim on 03.08.2017.
 */
public interface Partable<ColorType> {
    void setPartColor(ColorType color, int partStart, int partEnd);
}
