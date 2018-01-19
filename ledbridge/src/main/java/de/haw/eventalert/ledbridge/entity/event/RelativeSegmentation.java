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

    public int getCapacity() {
        return segmentation.length;
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


    public int getSegmentStartIndex(int segmentationIndex, int otherSegmentationCapacity) {
        //return (int) (otherSegmentationCapacity * ((double) relativeSegmentIndex / (segmentation.length)));
        if (segmentationIndex == 0)
            return 0;


        double ratio = otherSegmentationCapacity / getCapacity();
//        if(segmentationIndex == this.getCapacity()-1)
//            return (int) ((otherSegmentationCapacity - 1) -ratio+1);

        int offset = 0;
//        if(getCapacity() < (otherSegmentationCapacity - getCapacity()) && segmentationIndex >= 1) {
//            ratio++;
//        }
        int segmentStart = (int) Math.ceil(segmentationIndex * ratio);

        return segmentStart;
    }

    public int getSegmentEndIndex(int segmentationIndex, int otherSegmentationCapacity) {
        if (segmentationIndex == this.getCapacity() - 1)
            return (otherSegmentationCapacity - 1);


        int offset = 0;
        double ratio = otherSegmentationCapacity / segmentation.length;
        if (getCapacity() < (otherSegmentationCapacity - getCapacity())) {
            ratio++;
        }
        return (int) (getSegmentStartIndex(segmentationIndex, otherSegmentationCapacity) + ratio - 1);

        //int offset = (int) (ratio-1);
//        if(getCapacity() < (otherSegmentationCapacity - getCapacity())) {
//            offset +=  (int) Math.ceil( ratio);
//        }
        //int segmentEnd = (int) (Math.round(segmentationIndex * ratio) + offset);// offset;
        //return segmentEnd;

    }

}
