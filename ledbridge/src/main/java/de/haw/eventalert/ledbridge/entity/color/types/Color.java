package de.haw.eventalert.ledbridge.entity.color.types;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * Created by Tim on 12.05.2017.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RGB.class, name = "rgb"),
        @JsonSubTypes.Type(value = RGBW.class, name = "rgbw")
})
public interface Color extends Serializable {
    String[] asArray();

    String toHexString();
}
