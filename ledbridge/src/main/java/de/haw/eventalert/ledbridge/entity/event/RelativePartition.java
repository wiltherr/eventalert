package de.haw.eventalert.ledbridge.entity.event;

import de.haw.eventalert.ledbridge.entity.color.types.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class RelativePartition {

    private final Color[] partition;

    public RelativePartition(int relativeSize) {
        if (relativeSize < 0)
            throw new IllegalArgumentException("relativeSize is negative int");
        this.partition = new Color[relativeSize];
    }

    public boolean setPart(int partPosition, Color color) {
        if (partPosition < 0)
            throw new IllegalArgumentException("partPosition is negative int");
        if (partPosition >= partition.length)
            return false;
        partition[partPosition] = color;
        return true;
    }

    public List<Part<Color>> getAbsoulteParts(int absoulteSize) {
        List<Part<Color>> absoluteParts = new ArrayList<>();
        int partStartIdx = 0;
        int partEndIdx = 0;
        boolean partClosed = false;
        for (int relativePos = 0; relativePos < partition.length; relativePos++) {
            if (Objects.equals(partition[partStartIdx], partition[relativePos])) { //compare with color of partStart index
                partEndIdx++; //increase part
            } else {
                if (partition[partStartIdx] != null) {
                    //calculate abolute positions and create a new part
                    int absPartStart = (int) (absoulteSize * ((double) partStartIdx / (partition.length)));
                    int absPartEnd = (int) (absoulteSize * ((double) (partEndIdx + 1) / (partition.length)));
                    absoluteParts.add(new Part<>(partition[partStartIdx], absPartStart, absPartEnd));
                }
                //start a new part (reset indexes)
                partStartIdx = relativePos;
                partEndIdx = relativePos;
            }
        }
        return absoluteParts;
    }

    public int calculateAbsoultePartStart(int relPartPos, int absoluteSize) {
        return (int) (absoluteSize * ((double) relPartPos / (partition.length)));
    }

    public int calculateAbsoultePartEnd(int relPartPos, int absoluteSize) {
        absoluteSize = absoluteSize - 1;
        relPartPos = relPartPos + 1;
        return (int) ((absoluteSize) * ((double) (relPartPos) / (partition.length)));
    }

}
