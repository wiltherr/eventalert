package de.haw.eventalert.ledbridge.entity.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import de.haw.eventalert.ledbridge.entity.color.segmentation.ColorSegmentation;

@JsonTypeName("colorSegmentationEvent")
public class ColorSegmentationEvent extends LEDEvent {

    private ColorSegmentation colorSegmentation;

    public ColorSegmentationEvent() {
        super("colorSegmentationEvent");
    }


    public ColorSegmentation getColorSegmentation() {
        return colorSegmentation;
    }

    public void setColorSegmentation(ColorSegmentation colorSegmentation) {
        this.colorSegmentation = colorSegmentation;
    }
}
