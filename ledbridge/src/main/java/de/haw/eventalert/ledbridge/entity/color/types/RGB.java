package de.haw.eventalert.ledbridge.entity.color.types;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.haw.eventalert.ledbridge.entity.color.RGBImpl;

/**
 * Created by Tim on 12.05.2017.
 */
@JsonTypeName("rgb")
@JsonDeserialize(as = RGBImpl.class)
public interface RGB extends Color {
    int getR();

    void setR(int r);

    int getG();

    void setG(int g);

    int getB();

    void setB(int b);

    void setRGB(int r, int b, int g);
}
