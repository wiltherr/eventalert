package de.haw.eventalert.ledbridge.connector;


import de.haw.eventalert.ledbridge.entity.event.LEDEvent;

public interface LEDControllerConnector {
    boolean open();

    void close();

    void processEvent(LEDEvent ledEvent) throws LEDEventTypeNotSupportedException;

    void playStartEffect();
}
