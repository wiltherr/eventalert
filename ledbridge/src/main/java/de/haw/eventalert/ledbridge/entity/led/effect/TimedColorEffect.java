package de.haw.eventalert.ledbridge.entity.led.effect;


import de.haw.eventalert.ledbridge.entity.color.types.Color;
import de.haw.eventalert.ledbridge.entity.led.type.Dimmable;
import de.haw.eventalert.ledbridge.entity.led.type.Dyeable;

/**
 * Created by Tim on 04.05.2017.
 */
public
abstract class TimedColorEffect implements Dimmable, Dyeable<Color>, TimeLimited {
    private Color color;
    private int brightness;
    private long duration;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
