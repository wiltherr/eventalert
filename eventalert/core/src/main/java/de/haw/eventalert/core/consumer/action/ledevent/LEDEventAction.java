package de.haw.eventalert.core.consumer.action.ledevent;

import de.haw.eventalert.core.consumer.action.Action;
import de.haw.eventalert.ledbridge.entity.event.LEDEvent;

/**
 * A LEDEventAction is used to emit a {@link LEDEvent} to Kafka.
 */
public class LEDEventAction implements Action {

    private LEDEvent ledEvent;

    public LEDEventAction(LEDEvent ledEvent) {
        this.ledEvent = ledEvent;
    }

    @Override
    public String getName() {
        return "ledevent";
    }

    /**
     * produces the previously defined {@link LEDEvent} to kafka
     */
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
