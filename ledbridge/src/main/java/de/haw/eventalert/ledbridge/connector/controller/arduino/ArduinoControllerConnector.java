package de.haw.eventalert.ledbridge.connector.controller.arduino;

import de.haw.eventalert.ledbridge.connector.LEDControllerConnector;
import de.haw.eventalert.ledbridge.connector.controller.EffectableLEDControllerConnector;
import de.haw.eventalert.ledbridge.entity.color.Colors;
import de.haw.eventalert.ledbridge.entity.event.ColorEvent;
import de.haw.eventalert.ledbridge.entity.event.DimEvent;
import de.haw.eventalert.ledbridge.entity.event.TimedColorEvent;
import org.ardulink.core.Link;
import org.ardulink.core.convenience.Links;
import org.ardulink.util.URIs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tim on 12.05.2017.
 */
public class ArduinoControllerConnector extends EffectableLEDControllerConnector implements LEDControllerConnector {

    private static final Logger LOG = LoggerFactory.getLogger(ArduinoControllerConnector.class);

    private static final String SERIAL_PORT = "COM4";
    private static final String CONNECTION_URI = "ardulink://serial-jssc?port=" + SERIAL_PORT;

    private Link link;

    public ArduinoControllerConnector() {
        //super(true); Einkommentieren wenn die LED einen Zustand hat
    }

    private void connectToArduino(String connectionURI) {
        link = Links.getLink(URIs.newURI(connectionURI));
    }

    private void registerListeners() {
        try {
            link.addCustomListener(new ArduinoMessageLogger());
            link.addRplyListener(new ArduinoMessageLogger());
        } catch (IOException e) {
            LOG.error("Error adding listeners to aruduino link!", e);
        }
    }

    @Override
    public boolean open() {
        try {
            connectToArduino(CONNECTION_URI);
            registerListeners();
            LOG.info("arduino connection to link {} successful", CONNECTION_URI);
            return true;
        } catch (Exception e) {
            LOG.warn("arduino connection to link {} not successful", CONNECTION_URI, e);
            return false;
        }
    }

    @Override
    public void close() {
        if (link != null) {
            try {
                link.close();
                LOG.info("aurdino connection was closed successfully");
            } catch (IOException e) {
                LOG.error("error closeing aurdino connection", e);
            }
        }
    }

    @Override
    public void playStartEffect() {
        TimedColorEvent color1 = new TimedColorEvent();
        color1.setBrightness(10);
        color1.setColor(Colors.createRGBW(255, 0, 0, 0));
        color1.setDuration(100);
        TimedColorEvent color2 = new TimedColorEvent();

        color2.setBrightness(100);
        color2.setColor(Colors.createRGBW(0, 0, 255, 0));
        color2.setDuration(100);

        try {
            for (int i = 0; i < 5; i++) {
                onTimedColorEvent(color1);
                TimeUnit.MILLISECONDS.sleep(100);
                onTimedColorEvent(color2);
                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTimedColorEvent(TimedColorEvent timedColorEventedEvent) {
        sendMsg("rgbw/" + String.valueOf(timedColorEventedEvent.getDuration()) + "/" + String.join(",", timedColorEventedEvent.getColor().asArray()) + "/");
    }

    @Override
    public void onColorEvent(ColorEvent colorEvent) {
        sendMsg("colr/" + String.join(",", color.asArray()) + "/");
    }

    @Override
    public void onDimEvent(DimEvent dimEvent) {
        LOG.error("Unsupported event was called");
    }

    private void sendMsg(String message) {
        try {
            long id = link.sendCustomMessage(message);
            LOG.info("Message#{} send to arduino. Content: {}", id, message);
        } catch (IOException e) {
            LOG.error("Sending message to arduino failed. Content: {}", message, e);
        }
    }
}
