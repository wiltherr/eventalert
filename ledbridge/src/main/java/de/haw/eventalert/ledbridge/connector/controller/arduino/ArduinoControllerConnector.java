package de.haw.eventalert.ledbridge.connector.controller.arduino;

import de.haw.eventalert.ledbridge.connector.LEDControllerConnector;
import de.haw.eventalert.ledbridge.connector.controller.EffectableLEDControllerConnector;
import de.haw.eventalert.ledbridge.entity.color.Colors;
import de.haw.eventalert.ledbridge.entity.color.segmentation.ColorSegment;
import de.haw.eventalert.ledbridge.entity.color.segmentation.ColorSegmentation;
import de.haw.eventalert.ledbridge.entity.color.types.Color;
import de.haw.eventalert.ledbridge.entity.event.ColorEvent;
import de.haw.eventalert.ledbridge.entity.event.ColorSegmentationEvent;
import de.haw.eventalert.ledbridge.entity.event.DimEvent;
import de.haw.eventalert.ledbridge.entity.event.TimedColorEvent;
import org.ardulink.core.Link;
import org.ardulink.core.convenience.Links;
import org.ardulink.util.URIs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tim on 12.05.2017.
 */
public class ArduinoControllerConnector extends EffectableLEDControllerConnector implements LEDControllerConnector {

    private static final Logger LOG = LoggerFactory.getLogger(ArduinoControllerConnector.class);

    private static final String SERIAL_PORT = "COM4";
    private static final String CONNECTION_URI = "ardulink://serial-jssc?port=" + SERIAL_PORT;

    private static final long TIME_DELAY_MS = 50;

    private Link link;
    private final int numLEDs;

    public ArduinoControllerConnector(int numLEDs) {
        this.numLEDs = numLEDs;
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
        DimEvent dimEvent = new DimEvent();
        //init brightness
        dimEvent.setBrightness(10);

        Color colorRed = Colors.createRGBW(100, 0, 0, 0);
        Color colorGreen = Colors.createRGBW(0, 100, 0, 0);
        Color colorBlue = Colors.createRGBW(0, 0, 100, 0);
        Color colorPurple = Colors.createRGBW(100, 0, 100, 0);
        ColorSegmentationEvent colorSegmentationEvent = new ColorSegmentationEvent();
        colorSegmentationEvent.setColorSegmentation(ColorSegmentation.create(colorRed, colorPurple, colorBlue, colorGreen, colorRed, colorRed, colorRed));

        ColorSegmentationEvent colorSegmentationEvent2 = new ColorSegmentationEvent();
        ColorSegmentation colorSegmentation = ColorSegmentation.create(100);
        colorSegmentation.setSegment(0, colorGreen);
        colorSegmentation.setSegment(1, colorGreen);
        colorSegmentation.setSegment(2, colorGreen);
        colorSegmentation.setSegment(3, colorGreen);
        colorSegmentation.setSegment(99, colorGreen);
        colorSegmentation.setSegment(ColorSegment.create(colorPurple, 45, 55));
        colorSegmentationEvent2.setColorSegmentation(colorSegmentation);

        TimedColorEvent color1 = new TimedColorEvent();

        color1.setBrightness(10);
        color1.setColor(colorRed);
        color1.setDuration(10);
        TimedColorEvent color2 = new TimedColorEvent();
        color2.setBrightness(255);
        color2.setColor(colorPurple);
        color2.setDuration(10);

        try {
            for (int i = 0; i < 5; i++) {
                onTimedColorEvent(color1);
                TimeUnit.MILLISECONDS.sleep(color1.getDuration() + TIME_DELAY_MS);
                onTimedColorEvent(color2);
                TimeUnit.MILLISECONDS.sleep(color2.getDuration() + TIME_DELAY_MS);
            }

            onDimEvent(dimEvent);
            TimeUnit.MILLISECONDS.sleep(TIME_DELAY_MS);
            onColorSegmentationEvent(colorSegmentationEvent);
            TimeUnit.MILLISECONDS.sleep(TIME_DELAY_MS + 2000);
            onColorSegmentationEvent(colorSegmentationEvent2);
            TimeUnit.MILLISECONDS.sleep(TIME_DELAY_MS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTimedColorEvent(TimedColorEvent timedColorEventedEvent) {
        sendMsg("time/" + String.valueOf(timedColorEventedEvent.getDuration()) + "/" + timedColorEventedEvent.getBrightness() + "/" + timedColorEventedEvent.getColor().toHexString() + "/");
    }

    @Override
    public void onColorEvent(ColorEvent colorEvent) {
        sendMsg("colr/" + colorEvent.getColor().toHexString() + "/");
    }

    @Override
    public void onDimEvent(DimEvent dimEvent) {
        sendMsg("dimm/" + dimEvent.getBrightness() + "/");
    }

    @Override
    public void onColorSegmentationEvent(ColorSegmentationEvent colorSegmentationEvent) {
        List<ColorSegment> colorSegmentList = colorSegmentationEvent.getColorSegmentation().getSegments(this.numLEDs);
        LOG.error("errorrrrrrrrrrrrrrrrrrrrrrrrrrr: {}", colorSegmentList);
        colorSegmentList.stream().forEach(colorSegment -> {
            if (colorSegment.getColor() == null) {
                colorSegment.setColor(Colors.createRGBW(0, 0, 0, 0)); //TODO replace with default color
            }
            sendMsg("part/" + colorSegment.getStart() + "," + colorSegment.getEnd() + "/" + colorSegment.getColor().toHexString() + "/");
            try {
                TimeUnit.MILLISECONDS.sleep(TIME_DELAY_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }

    private void sendMsg(String message) {
        if (!message.endsWith("/")) {
            message = message + "/";
        }
        try {
            long id = link.sendCustomMessage(message);
            LOG.info("Message#{} send to arduino. Content: {}", id, message);
        } catch (IOException e) {
            LOG.error("Sending message to arduino failed. Content: {}", message, e);
        }
    }
}
