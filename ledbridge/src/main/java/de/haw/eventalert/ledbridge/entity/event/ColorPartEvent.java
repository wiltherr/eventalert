package de.haw.eventalert.ledbridge.entity.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import de.haw.eventalert.ledbridge.entity.color.types.Color;
import de.haw.eventalert.ledbridge.entity.event.type.Dyeable;

@JsonTypeName("colorPartEvent")
public class ColorPartEvent extends LEDEvent implements Dyeable<Color> {

    private int partStart;
    private int partEnd;
    private Color color;

    public ColorPartEvent() {
        super("colorPartEvent");
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }


    public void setPart(int partStart, int partEnd) {
        this.partStart = partStart;
        this.partEnd = partEnd;
    }


    public int getPartStart() {
        return partStart;
    }


    public int getPartEnd() {
        return partEnd;
    }
}
