package de.haw.eventalert.ledbridge.connector;

import de.haw.eventalert.ledbridge.entity.event.LEDEvent;

public class LEDEventTypeNotSupportedException extends Exception {
    private LEDEvent event;

    public LEDEventTypeNotSupportedException(LEDEvent event) {
        super("LEDControllerConnector does not support event type " + event.getType());
        this.event = event;
    }

    public LEDEvent getEvent() {
        return event;
    }


}
