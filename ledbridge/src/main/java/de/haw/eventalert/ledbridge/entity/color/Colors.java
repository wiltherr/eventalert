package de.haw.eventalert.ledbridge.entity.color;


import de.haw.eventalert.ledbridge.entity.color.types.RGB;
import de.haw.eventalert.ledbridge.entity.color.types.RGBW;

/**
 * Created by Tim on 12.05.2017.
 */
public class Colors {
    public static final int MAX = 255;
    public static final int MIN = 0;

    public static void checkValue(int colorVal) {
        if (colorVal < MIN || colorVal > MAX)
            throw new IllegalArgumentException("Value has to be between " + MIN + " and " + MAX);
    }

    public static void checkValues(int... colorVals) {
        for (int colorVal : colorVals) {
            checkValue(colorVal);
        }
    }

    public static RGBW createRGBW() {
        return new RGBWImpl(0, 0, 0, 0);
    }

    public static RGBW createRGBW(int r, int g, int b, int w) {
        return new RGBWImpl(r, g, b, w);
    }

    public static RGB createRGB() {
        return new RGBImpl(0, 0, 0);
    }

    /**
     * creates a {@link RGB} color of a {@link java.awt.Color}
     *
     * @param color java awt color
     * @return {@link de.haw.eventalert.ledbridge.entity.color.types.Color RGB color} for ledbridge
     */
    public static RGB createRGB(java.awt.Color color) {
        return new RGBImpl(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * creates a {@link RGBW} color of a {@link java.awt.Color}
     *
     * @param color java awt color
     * @param w     value for white led (0-255)
     * @return {@link de.haw.eventalert.ledbridge.entity.color.types.Color RGB color} for ledbridge
     */
    public static RGBW createRGBW(java.awt.Color color, int w) {
        return new RGBWImpl(color.getRed(), color.getGreen(), color.getBlue(), w);
    }
}
