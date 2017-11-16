package de.haw.eventalert.ledbridge.connector.controller.test;

import de.haw.eventalert.ledbridge.connector.LEDControllerConnector;
import de.haw.eventalert.ledbridge.entity.color.types.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LEDBridgeTestControllerConnector implements LEDControllerConnector, LEDControllerConnector.ColorLEDEventSupport {
    private static final Logger LOG = LoggerFactory.getLogger(LEDBridgeTestControllerConnector.class);

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
    public void setColor(Color color) {
        LOG.info("setColor: " + String.join(",", color.asArray()));
    }
}
