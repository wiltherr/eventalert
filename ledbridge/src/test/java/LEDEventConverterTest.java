import de.haw.eventalert.ledbridge.entity.color.Colors;
import de.haw.eventalert.ledbridge.entity.color.segmentation.ColorSegmentation;
import de.haw.eventalert.ledbridge.entity.color.types.Color;
import de.haw.eventalert.ledbridge.entity.event.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


public class LEDEventConverterTest {

    private Color color;
    private int brightness;
    private long timeInMs;
    private long targetLEDId;

    @BeforeEach
    void setUp() {
        color = Colors.createRGBW(255, 255, 255, 255);
        brightness = 100;
        timeInMs = 4211;
        targetLEDId = 12;
    }

    @Test
    void testConvertColorEvent() throws IOException {
        ColorEvent colorEvent = new ColorEvent();
        colorEvent.setTargetLEDId(targetLEDId);
        colorEvent.setColor(color);
        LEDEvent convertedEvent = convertAround(colorEvent);
        assertEquals(colorEvent.getTargetLEDId(), colorEvent.getTargetLEDId());
        assertEquals(colorEvent.getType(), convertedEvent.getType());
        assertTrue(convertedEvent instanceof ColorEvent);
        assertArrayEquals(color.asArray(), ((ColorEvent) convertedEvent).getColor().asArray());
    }

    @Test
    void testConvertColorPartEvent() throws IOException {
        ColorSegmentationEvent colorSegmentationEvent = new ColorSegmentationEvent();
        colorSegmentationEvent.setTargetLEDId(targetLEDId);
        ColorSegmentation colorSegmentation = ColorSegmentation.create(Colors.createRGBW(255, 0, 0, 0), null, Colors.createRGBW(0, 255, 0, 0), Colors.createRGBW(255, 0, 0, 0));
        colorSegmentationEvent.setColorSegmentation(colorSegmentation);

        LEDEvent convertedEvent = convertAround(colorSegmentationEvent);
        assertEquals(colorSegmentationEvent.getTargetLEDId(), colorSegmentationEvent.getTargetLEDId());
        assertEquals(colorSegmentationEvent.getType(), convertedEvent.getType());
        assertTrue(convertedEvent instanceof ColorSegmentationEvent);
        assertEquals(colorSegmentation, ((ColorSegmentationEvent) convertedEvent).getColorSegmentation());
    }

    @Test
    void testConvertDimEvent() throws IOException {
        DimEvent dimEvent = new DimEvent();
        dimEvent.setTargetLEDId(targetLEDId);
        dimEvent.setBrightness(brightness);
        LEDEvent convertedEvent = convertAround(dimEvent);
        assertEquals(dimEvent.getTargetLEDId(), dimEvent.getTargetLEDId());
        assertEquals(dimEvent.getType(), convertedEvent.getType());
        assertTrue(convertedEvent instanceof DimEvent);
        assertEquals(brightness, ((DimEvent) convertedEvent).getBrightness());
    }

    @Test
    void testConvertTimedColorEvent() throws IOException {
        TimedColorEvent timedColorEvent = new TimedColorEvent();
        timedColorEvent.setTargetLEDId(targetLEDId);
        timedColorEvent.setBrightness(brightness);
        timedColorEvent.setColor(color);
        timedColorEvent.setDuration(timeInMs);
        LEDEvent convertedEvent = convertAround(timedColorEvent);
        assertEquals(timedColorEvent.getTargetLEDId(), convertedEvent.getTargetLEDId());
        assertEquals(timedColorEvent.getType(), convertedEvent.getType());
        assertTrue(convertedEvent instanceof TimedColorEvent);
        assertEquals(brightness, ((TimedColorEvent) convertedEvent).getBrightness());
        assertArrayEquals(color.asArray(), ((TimedColorEvent) convertedEvent).getColor().asArray());
        assertEquals(timeInMs, ((TimedColorEvent) convertedEvent).getDuration());
    }

    private LEDEvent convertAround(LEDEvent ledEvent) throws IOException {
        return LEDEventConverter.toLEDEvent(LEDEventConverter.toJsonString(ledEvent));
    }
}
