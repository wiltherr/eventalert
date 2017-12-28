package de.haw.eventalert.core.consumer.action;

import de.haw.eventalert.core.consumer.action.example.ExampleAction;
import de.haw.eventalert.core.consumer.action.ledevent.LEDEventAction;
import de.haw.eventalert.ledbridge.entity.event.LEDEvent;

/**
 * factory class for {@link Action} interface
 */
public class Actions {
    public static Action createMyAction() {
        return new ExampleAction();
    }

    public static Action createLEDEventAction(LEDEvent ledEvent) {
        return new LEDEventAction(ledEvent);
    }
}
