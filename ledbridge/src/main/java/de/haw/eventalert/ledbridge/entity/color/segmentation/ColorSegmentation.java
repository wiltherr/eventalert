package de.haw.eventalert.ledbridge.entity.color.segmentation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.haw.eventalert.ledbridge.entity.color.types.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class ColorSegmentation {


    private Color[] segmentation;

    public ColorSegmentation(int capacity) {
        if (capacity <= 0)
            throw new IllegalArgumentException("capacity has to be greater than 0");
        this.segmentation = new Color[capacity];
    }

    @SuppressWarnings("unused")
        //only for json mapping
    ColorSegmentation() {
    }


    public ColorSegmentation(Color... colors) {
        if (colors.length == 0)
            throw new IllegalArgumentException("capacity has to be greater than 0");
        this.segmentation = colors;
    }

    public static ColorSegmentation create(int capacity) {
        return new ColorSegmentation(capacity);
    }

    public static ColorSegmentation create(Color... colors) {
        return new ColorSegmentation(colors);
    }

    @SuppressWarnings("unused") //only for json mapping
    public Color[] getSegmentation() {
        return segmentation;
    }

    @SuppressWarnings("unused") //only for json mapping
    public void setSegmentation(Color[] segmentation) {
        this.segmentation = segmentation;
    }

    public boolean setSegment(ColorSegment colorSegment) throws IllegalArgumentException {
        if (colorSegment.getStart() == colorSegment.getEnd()) {
            return this.setSegment(colorSegment.getStart(), colorSegment.getColor());
        }
        boolean colorInserted = true;
        for (int i = colorSegment.getStart(); colorInserted && i <= colorSegment.getEnd(); i++) {
            colorInserted = this.setSegment(i, colorSegment.getColor());
        }
        return colorInserted;
    }


    //TODO add setSegment(ColorSegment) and setSegments(List<ColorSegment>)
    public boolean setSegment(int position, Color color) throws IllegalArgumentException {
        if (position < 0)
            throw new IllegalArgumentException("position is negative int");
        if (position >= segmentation.length)
            return false;
        segmentation[position] = color;
        return true;
    }

    @JsonIgnore
    public int getCapacity() {
        return segmentation.length;
    }

    @JsonIgnore
    List<ColorSegment> getSegments() {
        List<ColorSegment> result = new ArrayList<>();
        int segmentStartIdx = 0;
        int segmentEndIdx = 0;
        boolean segmentDetected = false;
        for (int currentPos = 0; currentPos < segmentation.length; currentPos++) {
            int nextPos = currentPos + 1;
            if (nextPos < getCapacity() && Objects.equals(segmentation[currentPos], segmentation[nextPos])) {
                if (!segmentDetected) {
                    segmentStartIdx = currentPos;
                    segmentEndIdx = currentPos;
                    segmentDetected = true;
                } else {
                    segmentEndIdx++;
                }
            } else {
                if (segmentDetected)
                    result.add(ColorSegment.create(segmentation[currentPos], segmentStartIdx, segmentEndIdx + 1));
                else
                    result.add(ColorSegment.create(segmentation[currentPos], currentPos));
                segmentDetected = false;
            }
        }
        return result;
    }

    /**
     * converts the segments positions of this segmentation to an other segmentation capacity
     *
     * @param otherSegmentationCapacity capacity of other segmentation
     * @return a stream of {@link ColorSegment}s with modified start and end positions in ratio to capacity of the other segmentation.
     */
    public List<ColorSegment> getSegments(int otherSegmentationCapacity) {
        if (otherSegmentationCapacity < this.segmentation.length) {
            throw new IllegalArgumentException("otherSegmentationCapacity has to be greater or equal to this capacity.");
        }
        return getSegments().stream()
                .peek((colorSegment) -> { //calculate and change start and end positions of segments
                    colorSegment.setStart(calcSegmentStart(colorSegment.getStart(), otherSegmentationCapacity));
                    colorSegment.setEnd(calcSegmentEnd(colorSegment.getEnd(), otherSegmentationCapacity));
                }).collect(Collectors.toList());
    }

    private double calculateRatio(int otherSegmentationCapacity) {
        return (double) otherSegmentationCapacity / (segmentation.length);
    }

    private int calcSegmentStart(int startPosition, int otherSegmentationCapacity) {
        if (startPosition == 0)
            return 0;

        double ratio = calculateRatio(otherSegmentationCapacity);
        if (startPosition == segmentation.length - 1)
            return (int) (otherSegmentationCapacity - ratio + 1);

        int offset;
        if (ratio < 2)
            offset = 0;
//        else if(startPosition % ratio == 0)
//            offset = 1;
        else
            offset = 1;

        return (int) (startPosition * ratio + offset);
    }

    private int calcSegmentEnd(int endPosition, int otherSegmentationCapacity) {
        if (endPosition == segmentation.length - 1)
            return (otherSegmentationCapacity - 1);

        //int ratio = calculateRatio(otherSegmentationCapacity);
        double ratio = calculateRatio(otherSegmentationCapacity);
        int offset;

        if (ratio < 2)
            offset = 0;

        else
            offset = (int) ratio;

//        if (endPosition < 2) {
//            offset--;
//        }

        return (int) Math.round(endPosition * ratio) + offset;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColorSegmentation that = (ColorSegmentation) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(segmentation, that.segmentation);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(segmentation);
    }
}
