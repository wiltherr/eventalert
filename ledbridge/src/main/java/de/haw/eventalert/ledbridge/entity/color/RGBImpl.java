package de.haw.eventalert.ledbridge.entity.color;


import de.haw.eventalert.ledbridge.entity.color.types.RGB;

/**
 * Created by Tim on 30.04.2017.
 */
public class RGBImpl implements RGB {

    private int r;
    private int g;
    private int b;

    @SuppressWarnings("unused")
    RGBImpl() { //Jackson needs a default constructor
    }

    RGBImpl(int r, int g, int b) {
        Colors.checkValues(r, g, b);
        this.r = r;
        this.g = g;
        this.b = b;
    }


    public int getR() {
        return r;
    }


    public void setR(int r) {
        Colors.checkValue(r);
        this.r = r;
    }

    public int getG() {
        return g;
    }


    public void setG(int g) {
        Colors.checkValue(g);
        this.g = g;
    }


    public int getB() {
        return b;
    }


    public void setB(int b) {
        Colors.checkValue(b);
        this.b = b;
    }

    public void setRGB(int r, int b, int g) {
        Colors.checkValues(r, g, b);
        this.r = r;
        this.g = g;
        this.b = b;
    }


    public String[] asArray() {
        return new String[]{Integer.toString(getR()), Integer.toString(getG()), Integer.toString(getB())};
    }
}
