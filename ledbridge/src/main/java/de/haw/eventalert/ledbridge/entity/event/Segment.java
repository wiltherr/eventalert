package de.haw.eventalert.ledbridge.entity.event;

public class Segment<ColorType> {
    private ColorType color;

    private int startIndex;
    private int endIndex;

    public Segment(ColorType color, int startIndex, int endIndex) {
        this.color = color;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public Segment() {
    }

    public ColorType getColor() {
        return color;
    }

    public void setColor(ColorType color) {
        this.color = color;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    @Override
    public String toString() {
        return "Segment{" +
                "color=" + color +
                ", startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                '}';
    }
}
