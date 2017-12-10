package de.haw.eventalert.ledbridge.connector.controller;

import de.haw.eventalert.ledbridge.connector.LEDControllerConnector;
import de.haw.eventalert.ledbridge.connector.LEDEventTypeNotSupportedExecption;
import de.haw.eventalert.ledbridge.entity.color.types.Color;
import de.haw.eventalert.ledbridge.entity.event.ColorEvent;
import de.haw.eventalert.ledbridge.entity.event.DimEvent;
import de.haw.eventalert.ledbridge.entity.event.LEDEvent;
import de.haw.eventalert.ledbridge.entity.event.TimedColorEvent;
import de.haw.eventalert.ledbridge.entity.led.DyeableLed;

public abstract class EffectableLEDControllerConnector extends DyeableLed<Color> implements LEDControllerConnector {

    private boolean autoSetValues;

    public EffectableLEDControllerConnector() {
        this.autoSetValues = false;
    }

    public EffectableLEDControllerConnector(boolean autoSetValues) {
        this.autoSetValues = autoSetValues;
    }

    @Override
    public void processEvent(LEDEvent ledEvent) throws LEDEventTypeNotSupportedExecption {
        if (ledEvent instanceof ColorEvent) {
            if (autoSetValues) this.setColor(((ColorEvent) ledEvent).getColor());
            onColorEvent((ColorEvent) ledEvent);
        } else if (ledEvent instanceof DimEvent) {
            if (autoSetValues) this.setBrightness(((DimEvent) ledEvent).getBrightness());
            onDimEvent((DimEvent) ledEvent);
        } else if (ledEvent instanceof TimedColorEvent) {
            if (autoSetValues) {
                this.setColor(((TimedColorEvent) ledEvent).getColor());
                this.setBrightness(((TimedColorEvent) ledEvent).getBrightness());
            }
            onTimedColorEvent((TimedColorEvent) ledEvent);
        } else {
            throw new LEDEventTypeNotSupportedExecption(ledEvent);
        }
    }

    public abstract void onTimedColorEvent(TimedColorEvent timedColorEventedEvent);

    public abstract void onColorEvent(ColorEvent colorEvent);

    public abstract void onDimEvent(DimEvent dimEvent);
}
