package de.haw.eventalert.ledbridge.connector.controller;

import de.haw.eventalert.ledbridge.connector.LEDControllerConnector;
import de.haw.eventalert.ledbridge.connector.LEDEventTypeNotSupportedException;
import de.haw.eventalert.ledbridge.entity.color.types.Color;
import de.haw.eventalert.ledbridge.entity.event.*;
import de.haw.eventalert.ledbridge.entity.led.DyeableLed;

public abstract class EffectableLEDControllerConnector extends DyeableLed<Color> implements LEDControllerConnector {

    public EffectableLEDControllerConnector() {
    }


    @Override
    public void processEvent(LEDEvent ledEvent) throws LEDEventTypeNotSupportedException {
        if (ledEvent instanceof ColorEvent) {
            onColorEvent((ColorEvent) ledEvent);
        } else if (ledEvent instanceof ColorPartEvent) {
            onColorPartEvent((ColorPartEvent) ledEvent);
        } else if (ledEvent instanceof DimEvent) {
            onDimEvent((DimEvent) ledEvent);
        } else if (ledEvent instanceof TimedColorEvent) {
            onTimedColorEvent((TimedColorEvent) ledEvent);
        } else {
            throw new LEDEventTypeNotSupportedException(ledEvent);
        }
    }

    public abstract void onTimedColorEvent(TimedColorEvent timedColorEventedEvent);

    public abstract void onColorEvent(ColorEvent colorEvent);

    public abstract void onDimEvent(DimEvent dimEvent);

    public abstract void onColorPartEvent(ColorPartEvent colorPartEvent);
}
