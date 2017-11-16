package de.haw.eventalert.ledbridge.connector;

import de.haw.eventalert.ledbridge.entity.color.types.Color;

public interface LEDControllerConnector {
    boolean open();

    void close();

    interface ColorLEDEventSupport {
        void setColor(Color color);
    }
    //void setPartColor(Color color, Part)
}
