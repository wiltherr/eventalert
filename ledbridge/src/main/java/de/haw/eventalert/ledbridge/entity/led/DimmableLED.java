package de.haw.eventalert.ledbridge.entity.led;

import de.haw.eventalert.ledbridge.entity.color.Brightness;
import de.haw.eventalert.ledbridge.entity.event.type.Dimmable;

/**
 * Created by Tim on 30.04.2017.
 */
public abstract class DimmableLED implements Dimmable {

    protected int brightness;

    public void setOn() {
        this.setBrightness(Brightness.MAX);
    }

    public void setOff() {
        this.setBrightness(Brightness.MIN);
    }

    public boolean isOn() {
        return brightness > Brightness.MIN;
    }

    @Override
    public int getBrightness() {
        return brightness;
    }

    @Override
    public void setBrightness(int brightness) {
        Brightness.checkValue(brightness);
        this.brightness = brightness;
    }
}
