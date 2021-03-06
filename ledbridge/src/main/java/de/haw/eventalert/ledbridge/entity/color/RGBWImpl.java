package de.haw.eventalert.ledbridge.entity.color;


import de.haw.eventalert.ledbridge.entity.color.types.RGBW;

/**
 * Created by Tim on 12.05.2017.
 */
public class RGBWImpl extends RGBImpl implements RGBW {

    private int w;

    @SuppressWarnings("unused")
    RGBWImpl() {  //Jackson needs a default constructor
    }

    RGBWImpl(int r, int g, int b, int w) {
        super(r, g, b);
        this.w = w;
    }

    public RGBWImpl(int r, int g, int b) {
        this(r, g, b, 0);
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        Colors.checkValue(w);
        this.w = w;
    }

    public void setRGBW(int r, int g, int b, int w) {
        super.setRGB(r, g, b);
        Colors.checkValue(w);
        this.w = w;
    }

    @Override
    public String[] asArray() {
        return new String[]{Integer.toString(getR()), Integer.toString(getG()), Integer.toString(getB()), Integer.toString(getW())};
    }

    @Override
    public String toHexString() {
        return String.format("%02X%02X%02X%02X", getR(), getG(), getB(), getW());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RGBWImpl rgbw = (RGBWImpl) o;

        return w == rgbw.w;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + w;
        return result;
    }
}
