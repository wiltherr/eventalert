package de.haw.eventalert.ledbridge.entity.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.haw.eventalert.ledbridge.core.util.Utils;

import java.io.IOException;

public class LEDEventConverter {

    public static LEDEvent toLEDEvent(String jsonLEDEvent) throws IOException {
        return Utils.jsonMapper.readValue(jsonLEDEvent, LEDEvent.class);
    }

    public static String toJsonString(LEDEvent ledEvent) throws JsonProcessingException {
        return Utils.jsonMapper.writeValueAsString(ledEvent);
    }
}
