package de.haw.eventalert.ledbridge.entity.event;

import de.haw.eventalert.ledbridge.entity.color.types.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ColorSegmentation {

    private final Color[] segmentation;

    public ColorSegmentation(int capacity) {
        if (capacity < 0)
            throw new IllegalArgumentException("capacity is negative int");
        this.segmentation = new Color[capacity];
    }

    public boolean setSegment(int position, Color color) {
        if (position < 0)
            throw new IllegalArgumentException("position is negative int");
        if (position >= segmentation.length)
            return false;
        segmentation[position] = color;
        return true;
    }

    public int getCapacity() {
        return segmentation.length;
    }

    public List<ColorSegment> getSegments() {
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

    public List<ColorSegment> getSegments(int otherSegmentationCapacity) {
        List<ColorSegment> absoluteParts = new ArrayList<>();
        int segmentStartIndex = 0;
        int segmentEndIdx = 0;
        Color currentColor = null;
        for (int segmentationPos = 0; segmentationPos < segmentation.length; segmentationPos++) {
            if (Objects.equals(segmentation[segmentStartIndex], currentColor)) { //compare with color of partStart index
                currentColor = segmentation[segmentationPos];
                segmentEndIdx++; //increase part
            } else {
                if (currentColor != null) {
                    //calculate abolute positions and create a new part
                    int absPartStart = calcSegmentStartIndex(segmentStartIndex, otherSegmentationCapacity);
                    int absPartEnd = calcSegmentEndIndex(segmentEndIdx, otherSegmentationCapacity);
                    absoluteParts.add(ColorSegment.create(segmentation[segmentStartIndex], absPartStart, absPartEnd));
                }
                currentColor = segmentation[segmentationPos];
                //start a new part (reset indexes)
                segmentStartIndex = segmentationPos;
                segmentEndIdx = segmentationPos;
            }
        }
        return absoluteParts;
    }

    private int calculateRatio(int otherSegmentationCapacity) {
        return Math.round(otherSegmentationCapacity / (getCapacity() - 1));
    }

    int calcSegmentStartIndex(int segmentationIndex, int otherSegmentationCapacity) {
        if (segmentationIndex == 0)
            return 0;

        int ratio = calculateRatio(otherSegmentationCapacity);
        int offset;
        if (ratio < 2)
            offset = 0;
        else
            offset = 1;

        return segmentationIndex * ratio + offset;
    }

    int calcSegmentEndIndex(int segmentationIndex, int otherSegmentationCapacity) {
        if (segmentationIndex == this.getCapacity() - 1)
            return (otherSegmentationCapacity - 1);

        int ratio = calculateRatio(otherSegmentationCapacity);
        int offset;
        if (ratio < 2)
            offset = 0;
        else
            offset = ratio;

        return segmentationIndex * ratio + offset;
    }

}
