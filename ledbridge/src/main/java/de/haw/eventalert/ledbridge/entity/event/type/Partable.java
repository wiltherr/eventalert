package de.haw.eventalert.ledbridge.entity.event.type;

/**
 * Created by Tim on 03.08.2017.
 */
public interface Partable<ColorType> {
    void setPart(int position, ColorType color);

    ColorType getPart(int position);

    int getPartStart();

    int getPartEnd();
}
