package de.haw.eventalert.ledbridge.core;

import de.haw.eventalert.ledbridge.connector.controller.arduino.ArduinoControllerConnector;

public class LEDBridge {
    public static void main(String[] args) {
        LEDManager ledManager = LEDManager.getInstance();
        //Register ControllerConnectors
        ledManager.registerControllerConnector(new ArduinoControllerConnector(), 0L);
        //ledManager.registerControllerConnector(new LEDBridgeTestControllerConnector(), 1L);
        //Start manager
        ledManager.start();
    }
}
