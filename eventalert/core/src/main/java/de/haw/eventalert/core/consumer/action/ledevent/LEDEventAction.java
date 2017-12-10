package de.haw.eventalert.core.consumer.action.ledevent;

import de.haw.eventalert.core.consumer.action.Action;
import de.haw.eventalert.ledbridge.entity.event.LEDEvent;

/**
 * Created by Tim on 12.09.2017.
 */
public class LEDEventAction implements Action { //TODO make a real LEDEventAction

    private LEDEvent ledEvent;

    public LEDEventAction(LEDEvent ledEvent) {
        this.ledEvent = ledEvent;
    }

    @Override
    public String getName() {
        return "ledevent"; //TODO
    }

    @Override
    public void runAction() {
        LEDEventProducer.getInstance().emit(ledEvent);
    }

    @Override
    public String getConfigurationForLog() {
        return ledEvent.getType();
    }

    public LEDEvent getLedEvent() {
        return ledEvent;
    }

    public void setLedEvent(LEDEvent ledEvent) {
        this.ledEvent = ledEvent;
    }
}
