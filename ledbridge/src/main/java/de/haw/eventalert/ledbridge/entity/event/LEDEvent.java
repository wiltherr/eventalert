package de.haw.eventalert.ledbridge.entity.event;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DimEvent.class, name = "dimEvent"),
        @JsonSubTypes.Type(value = ColorEvent.class, name = "colorEvent"),
        @JsonSubTypes.Type(value = ColorPartEvent.class, name = "colorPartEvent"),
        @JsonSubTypes.Type(value = TimedColorEvent.class, name = "timedColorEvent")
})
public abstract
class LEDEvent implements Serializable {

    private String type;

    private long targetLEDId;

    public LEDEvent(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTargetLEDId() {
        return targetLEDId;
    }

    public void setTargetLEDId(long targetLEDId) {
        this.targetLEDId = targetLEDId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LEDEvent ledEvent = (LEDEvent) o;

        if (targetLEDId != ledEvent.targetLEDId) return false;
        return type != null ? type.equals(ledEvent.type) : ledEvent.type == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (int) (targetLEDId ^ (targetLEDId >>> 32));
        return result;
    }
}

