package de.haw.eventalert.ledbridge.entity.color;

/**
 * Created by Tim on 12.05.2017.
 */
public class Brightness {
    public static final int MAX = 100;
    public static final int MIN = 0;

    public static void checkValue(int brightnessVal) {
        if (brightnessVal < MIN || brightnessVal > MAX)
            throw new IllegalArgumentException("Value have to be between " + MIN + " and " + MAX);
    }
}
