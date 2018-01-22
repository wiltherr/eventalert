package de.haw.eventalert.ledbridge;

import de.haw.eventalert.ledbridge.connector.controller.arduino.ArduinoControllerConnector;
import de.haw.eventalert.ledbridge.core.LEDManager;

public class RunLEDBridge {
    public static void main(String[] args) {
        LEDManager ledManager = LEDManager.getInstance();
        //Register ControllerConnectors
        ledManager.registerControllerConnector(new ArduinoControllerConnector(144), 0L);
        //ledManager.registerControllerConnector(new LEDBridgeTestControllerConnector(), 0L);
        //Start manager
        ledManager.start();
    }
}
