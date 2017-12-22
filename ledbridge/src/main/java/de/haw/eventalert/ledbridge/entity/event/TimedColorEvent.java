package de.haw.eventalert.ledbridge.entity.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import de.haw.eventalert.ledbridge.entity.color.types.Color;
import de.haw.eventalert.ledbridge.entity.led.effect.TimeLimited;
import de.haw.eventalert.ledbridge.entity.led.type.Dimmable;
import de.haw.eventalert.ledbridge.entity.led.type.Dyeable;

@JsonTypeName("timedColorEvent")
public class TimedColorEvent extends LEDEvent implements Dimmable, Dyeable<Color>, TimeLimited {
    private Color color;
    private int brightness;
    private long duration;

    public TimedColorEvent() {
        super("timedColorEvent");
    }

    @Override
    public Color getColor() {

        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int getBrightness() {
        return brightness;
    }

    @Override
    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TimedColorEvent that = (TimedColorEvent) o;

        if (brightness != that.brightness) return false;
        if (duration != that.duration) return false;
        return color != null ? color.equals(that.color) : that.color == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + brightness;
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        return result;
    }
}