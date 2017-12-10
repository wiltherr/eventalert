package de.haw.eventalert.ledbridge.connector;

import de.haw.eventalert.ledbridge.entity.event.LEDEvent;
import lombok.Getter;

@Getter
public class LEDEventTypeNotSupportedExecption extends Exception {
    LEDEvent event;

    public LEDEventTypeNotSupportedExecption(LEDEvent event) {
        super("LEDControllerConnector does not support event type ".concat(event.getType()));
        this.event = event;
    }
}
