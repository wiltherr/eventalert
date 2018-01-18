package de.haw.eventalert.ledbridge.entity.event;

import de.haw.eventalert.ledbridge.entity.color.types.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class RelativeSegmentation {

    private final Color[] segmentation;

    public RelativeSegmentation(int partitionSize) {
        if (partitionSize < 0)
            throw new IllegalArgumentException("partitionSize is negative int");
        this.segmentation = new Color[partitionSize];
    }

    public boolean setSegment(int partPosition, Color color) {
        if (partPosition < 0)
            throw new IllegalArgumentException("partPosition is negative int");
        if (partPosition >= segmentation.length)
            return false;
        segmentation[partPosition] = color;
        return true;
    }

    public List<Segment<Color>> getSegments(int otherSegmentationCapacity) {
        List<Segment<Color>> absoluteParts = new ArrayList<>();
        int partStartIdx = 0;
        int partEndIdx = 0;
        boolean partClosed = false;
        for (int relativePos = 0; relativePos < segmentation.length; relativePos++) {
            if (Objects.equals(segmentation[partStartIdx], segmentation[relativePos])) { //compare with color of partStart index
                partEndIdx++; //increase part
            } else {
                if (segmentation[partStartIdx] != null) {
                    //calculate abolute positions and create a new part
                    int absPartStart = (int) (otherSegmentationCapacity * ((double) partStartIdx / (segmentation.length)));
                    int absPartEnd = (int) (otherSegmentationCapacity * ((double) (partEndIdx + 1) / (segmentation.length)));
                    absoluteParts.add(new Segment<>(segmentation[partStartIdx], absPartStart, absPartEnd));
                }
                //start a new part (reset indexes)
                partStartIdx = relativePos;
                partEndIdx = relativePos;
            }
        }
        return absoluteParts;
    }

    public int getSegmentStartIndex(int relativeSegmentIndex, int otherSegmentationCapacity) {
        return (int) (otherSegmentationCapacity * ((double) relativeSegmentIndex / (segmentation.length)));
    }

    public int getSegmentEndIndex(int relPartPos, int absoluteSize) {
        if (relPartPos == 0 && segmentation.length < absoluteSize) {
            relPartPos = 1;
        }
        if (relPartPos == segmentation.length - 1) {
            return absoluteSize - 1;
        }
        return (int) (relPartPos * ((double) absoluteSize / segmentation.length));
    }

}
