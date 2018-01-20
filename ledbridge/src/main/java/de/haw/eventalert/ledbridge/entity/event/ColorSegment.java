package de.haw.eventalert.ledbridge.entity.event;

import de.haw.eventalert.ledbridge.entity.color.types.Color;

public class ColorSegment {
    private Color color;

    private int positionStart;
    private int positionEnd;

    public ColorSegment(Color color, int positionStart, int positionEnd) {
        this.color = color;
        this.positionStart = positionStart;
        this.positionEnd = positionEnd;
    }

    public ColorSegment(Color color, int position) {
        this(color, position, position);
    }

    public ColorSegment() {
    }

    public static ColorSegment create(Color color, int positionStart, int positionEnd) {
        return new ColorSegment(color, positionStart, positionEnd);
    }

    public static ColorSegment create(Color color, int position) {
        return ColorSegment.create(color, position, position);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getPositionStart() {
        return positionStart;
    }

    public void setPositionStart(int positionStart) {
        this.positionStart = positionStart;
    }

    public int getPositionEnd() {
        return positionEnd;
    }

    public void setPositionEnd(int positionEnd) {
        this.positionEnd = positionEnd;
    }

    @Override
    public String toString() {
        return "ColorSegment{" +
                "color=" + color +
                ", positionStart=" + positionStart +
                ", positionEnd=" + positionEnd +
                '}';
    }
}
