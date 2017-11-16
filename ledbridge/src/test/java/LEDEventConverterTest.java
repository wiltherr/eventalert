import de.haw.eventalert.ledbridge.entity.color.Colors;
import de.haw.eventalert.ledbridge.entity.color.types.Color;
import de.haw.eventalert.ledbridge.entity.event.ColorLEDEvent;
import de.haw.eventalert.ledbridge.entity.event.LEDEvent;
import de.haw.eventalert.ledbridge.entity.event.LEDEventConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


public class LEDEventConverterTest {

    private Color color;
    private ColorLEDEvent ledEvent;

    @BeforeEach
    void init() {
        color = Colors.createRGBW(255, 255, 255, 255);
        ColorLEDEvent colorLEDEvent = new ColorLEDEvent();
        colorLEDEvent.setColor(color);
        ledEvent = colorLEDEvent;
    }

    @Test
    void testConvert() throws IOException {
        String jsonString = LEDEventConverter.toJsonString(ledEvent);
        LEDEvent convertedEvent = LEDEventConverter.toLEDEvent(jsonString);
        assertTrue(convertedEvent instanceof ColorLEDEvent);
        assertEquals(ledEvent.getType(), convertedEvent.getType());
        assertArrayEquals(color.asArray(), ((ColorLEDEvent) convertedEvent).getColor().asArray());
    }
}
