package de.haw.eventalert.ledbridge.entity.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import de.haw.eventalert.ledbridge.entity.led.type.Dimmable;
@JsonTypeName("dimEvent")
public class DimEvent extends LEDEvent implements Dimmable {
    private int brightness;

    public DimEvent() {
        super("dimEvent");
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DimEvent dimEvent = (DimEvent) o;

        return brightness == dimEvent.brightness;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + brightness;
        return result;
    }
}
