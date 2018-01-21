package de.haw.eventalert.ledbridge.connector.controller.test;

import de.haw.eventalert.ledbridge.connector.LEDControllerConnector;
import de.haw.eventalert.ledbridge.connector.LEDEventTypeNotSupportedException;
import de.haw.eventalert.ledbridge.connector.controller.EffectableLEDControllerConnector;
import de.haw.eventalert.ledbridge.entity.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LEDBridgeTestControllerConnector extends EffectableLEDControllerConnector implements LEDControllerConnector {
    private static final Logger LOG = LoggerFactory.getLogger(LEDBridgeTestControllerConnector.class);

    public LEDBridgeTestControllerConnector() {
    }

    @Override
    public boolean open() {
        LOG.info("open");
        return true;
    }

    @Override
    public void close() {
        LOG.info("closed");
    }

    @Override
    public void playStartEffect() {
        LOG.info("Playing startup sequence");
    }

    @Override
    public void processEvent(LEDEvent ledEvent) throws LEDEventTypeNotSupportedException {
        LOG.info("New ledEvent from type: {}", ledEvent.getType());
    }

    @Override
    public void onTimedColorEvent(TimedColorEvent timedColorEventedEvent) {
        LOG.info("Color timed color event: {} color, {} brigthness, {} duration", color.asArray(), brightness, timedColorEventedEvent.getDuration());
    }

    @Override
    public void onColorEvent(ColorEvent colorEvent) {
        LOG.info("Color changed to: {}", (Object) color.asArray());
    }

    @Override
    public void onDimEvent(DimEvent dimEvent) {
        LOG.info("Brigthness change to: {}", brightness);
    }

    @Override
    public void onColorSegmentationEvent(ColorSegmentationEvent colorSegmentationEvent) {
//        LOG.info("Color part event: {} color, {} partStart, {} partEnd", color.asArray(), colorSegmentationEvent.getPartStart(), colorSegmentationEvent.getPartEnd());
    }
}
