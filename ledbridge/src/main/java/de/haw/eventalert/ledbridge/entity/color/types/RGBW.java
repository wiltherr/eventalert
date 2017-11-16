package de.haw.eventalert.ledbridge.entity.color.types;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.haw.eventalert.ledbridge.entity.color.RGBWImpl;

/**
 * Created by Tim on 12.05.2017.
 */
@JsonTypeName("rgbw")
@JsonDeserialize(as = RGBWImpl.class)
public interface RGBW extends RGB {
    int getW();

    void setW(int w);

    void setRGBW(int r, int g, int b, int w);
}
