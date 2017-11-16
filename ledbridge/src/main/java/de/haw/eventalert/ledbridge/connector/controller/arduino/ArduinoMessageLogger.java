package de.haw.eventalert.ledbridge.connector.controller.arduino;

import org.ardulink.core.events.CustomEvent;
import org.ardulink.core.events.CustomListener;
import org.ardulink.core.events.RplyEvent;
import org.ardulink.core.events.RplyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Tim on 13.05.2017.
 */
public class ArduinoMessageLogger implements RplyListener, CustomListener {

    private static final Logger LOG = LoggerFactory.getLogger(ArduinoMessageLogger.class);

    @Override
    public void rplyReceived(RplyEvent e) {
        if (e.isOk())
            LOG.info("Message#{} was processed by arduino.", e.getId());
        else
            LOG.error("Message#{} could not be processed by arduino.", e.getId());
    }

    @Override
    public void customEventReceived(CustomEvent e) {
        if (e.getMessage().startsWith("log/")) {
            String msg = e.getMessage().substring(4);
            LOG.info("Arduino Log: {}", msg);
        } else {
            LOG.error("Unkown custom event from arduino. Content: {}", e.getMessage());
        }
    }
}
