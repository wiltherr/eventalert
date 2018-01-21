package de.haw.eventalert.ledbridge.entity.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import de.haw.eventalert.ledbridge.entity.color.segmentation.ColorSegmentation;

@JsonTypeName("colorSegmentationEvent")
public class ColorSegmentationEvent extends LEDEvent {

    private ColorSegmentation colorSegmentation;
    private boolean nullColorIntentionally;

    public ColorSegmentationEvent() {
        super("colorSegmentationEvent");
    }


    public ColorSegmentation getColorSegmentation() {
        return colorSegmentation;
    }

    public void setColorSegmentation(ColorSegmentation colorSegmentation) {
        this.colorSegmentation = colorSegmentation;
    }


    public boolean isNullColorIntentionally() {
        return nullColorIntentionally;
    }

    public void setNullColorIntentionally(boolean nullColorIntentionally) {
        this.nullColorIntentionally = nullColorIntentionally;
    }
}
