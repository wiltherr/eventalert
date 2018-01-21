package de.haw.eventalert.ledbridge.entity.color.segmentation;

import de.haw.eventalert.ledbridge.entity.color.types.Color;

public class ColorSegment {
    private Color color;

    private int start;
    private int end;

    public ColorSegment(Color color, int start, int end) {
        this.color = color;
        this.start = start;
        this.end = end;
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

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColorSegment that = (ColorSegment) o;

        if (start != that.start) return false;
        if (end != that.end) return false;
        return color != null ? color.equals(that.color) : that.color == null;
    }

    @Override
    public int hashCode() {
        int result = color != null ? color.hashCode() : 0;
        result = 31 * result + start;
        result = 31 * result + end;
        return result;
    }

    @Override
    public String toString() {
        return "ColorSegment{" +
                "color=" + color +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
