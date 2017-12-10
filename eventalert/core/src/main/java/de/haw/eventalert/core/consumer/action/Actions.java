package de.haw.eventalert.core.consumer.action;

import de.haw.eventalert.core.consumer.action.example.MyAction;
import de.haw.eventalert.core.consumer.action.ledevent.LEDEventAction;
import de.haw.eventalert.ledbridge.entity.event.LEDEvent;

/**
 * Created by Tim on 12.09.2017.
 */
public class Actions {
    public static Action createMyAction() {
        return new MyAction();
    }

    public static Action createLEDEventAction(LEDEvent ledEvent) {
        return new LEDEventAction(ledEvent);
    }
}
