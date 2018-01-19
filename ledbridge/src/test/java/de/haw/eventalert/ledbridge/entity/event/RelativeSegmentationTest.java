package de.haw.eventalert.ledbridge.entity.event;

import de.haw.eventalert.ledbridge.entity.color.Colors;
import de.haw.eventalert.ledbridge.entity.color.types.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RelativeSegmentationTest {


    public static final int SIZE = 10;
    public static final int SIZE_BIGGER = SIZE + 1;
    public static final int SIZE_DOUBLE = SIZE * 2;
    public static final int SIZE_TENFOLD = SIZE * 10;

    public static final Color TEST_COLOR_1 = Colors.createRGBW(255, 0, 0, 0);
    public static final Color TEST_COLOR_2 = Colors.createRGBW(0, 255, 0, 0);
    public static final Color TEST_COLOR_3 = Colors.createRGBW(0, 0, 255, 0);

    RelativeSegmentation relativeSegmentation;

    @BeforeEach
    void setUp() {
        relativeSegmentation = new RelativeSegmentation(SIZE);
    }

    void printSegmentation(RelativeSegmentation segmentation, int absSize) {
        for (int i = 0; i < segmentation.getCapacity(); i++) {
            int start = segmentation.getSegmentStartIndex(i, absSize);
            int end = segmentation.getSegmentEndIndex(i, absSize);
            System.out.printf("%d: %d/%d (%d)%n", i, start, end, (end - start) + 1);
        }
    }

    @Test
    void calculateAbsoultePart() {
        System.out.println("10 - Same capacity");
        printSegmentation(relativeSegmentation, SIZE);
        System.out.println("11 - capacity+1");
        printSegmentation(relativeSegmentation, SIZE_BIGGER);
        System.out.println("20 - double");
        printSegmentation(relativeSegmentation, SIZE_DOUBLE);
        System.out.println("33 - ");
        printSegmentation(relativeSegmentation, 33); //TODO komische ergebnisse bei rel 7 und rel 8
        System.out.println("47 - ");
        printSegmentation(relativeSegmentation, 47);
        System.out.println("249 - ");
        printSegmentation(relativeSegmentation, 249);


        assertEquals(0, relativeSegmentation.getSegmentStartIndex(32, 33));

        //same capacity
        assertEquals(0, relativeSegmentation.getSegmentStartIndex(0, SIZE));
        assertEquals(0, relativeSegmentation.getSegmentEndIndex(0, SIZE));

        assertEquals(4, relativeSegmentation.getSegmentStartIndex(4, SIZE));
        assertEquals(4, relativeSegmentation.getSegmentEndIndex(4, SIZE));

        assertEquals(9, relativeSegmentation.getSegmentStartIndex(9, SIZE));
        assertEquals(9, relativeSegmentation.getSegmentEndIndex(9, SIZE));

        //capicity + 1 (odd)
//        assertEquals(0, relativeSegmentation.getSegmentStartIndex(0,SIZE_BIGGER));
//        assertEquals(0, relativeSegmentation.getSegmentEndIndex(0,SIZE_BIGGER));
//
//        assertEquals(1, relativeSegmentation.getSegmentStartIndex(1,SIZE_BIGGER));
//        assertEquals(1, relativeSegmentation.getSegmentEndIndex(1,SIZE_BIGGER));
//
//        assertEquals(2, relativeSegmentation.getSegmentStartIndex(2,SIZE_BIGGER));
//        assertEquals(2, relativeSegmentation.getSegmentEndIndex(2,SIZE_BIGGER));
//
//        assertEquals(3, relativeSegmentation.getSegmentStartIndex(3,SIZE_BIGGER));
//        assertEquals(3, relativeSegmentation.getSegmentEndIndex(3,SIZE_BIGGER));
//
//        assertEquals(4, relativeSegmentation.getSegmentStartIndex(4,SIZE_BIGGER));
//        assertEquals(4, relativeSegmentation.getSegmentEndIndex(4,SIZE_BIGGER));
//
//        assertEquals(5, relativeSegmentation.getSegmentStartIndex(5,SIZE_BIGGER));
//        assertEquals(5, relativeSegmentation.getSegmentEndIndex(5,SIZE_BIGGER));
//
//        assertEquals(6, relativeSegmentation.getSegmentStartIndex(6,SIZE_BIGGER));
//        assertEquals(6, relativeSegmentation.getSegmentEndIndex(6,SIZE_BIGGER));
//
//        assertEquals(7, relativeSegmentation.getSegmentStartIndex(7,SIZE_BIGGER));
//        assertEquals(7, relativeSegmentation.getSegmentEndIndex(7,SIZE_BIGGER));
//
//        assertEquals(8, relativeSegmentation.getSegmentStartIndex(8,SIZE_BIGGER));
//        assertEquals(8, relativeSegmentation.getSegmentEndIndex(8,SIZE_BIGGER));
//
//        assertEquals(9, relativeSegmentation.getSegmentStartIndex(9,SIZE_BIGGER));
//        assertEquals(10, relativeSegmentation.getSegmentEndIndex(9,SIZE_BIGGER));

        //double capicity
        assertEquals(0, relativeSegmentation.getSegmentStartIndex(0, SIZE_DOUBLE));
        assertEquals(1, relativeSegmentation.getSegmentEndIndex(0, SIZE_DOUBLE));

        assertEquals(8, relativeSegmentation.getSegmentStartIndex(4, SIZE_DOUBLE));
        assertEquals(9, relativeSegmentation.getSegmentEndIndex(4, SIZE_DOUBLE));

        assertEquals(18, relativeSegmentation.getSegmentStartIndex(9, SIZE_DOUBLE));
        assertEquals(19, relativeSegmentation.getSegmentEndIndex(9, SIZE_DOUBLE));


        //tenfold capicity
        assertEquals(0, relativeSegmentation.getSegmentStartIndex(0, SIZE_TENFOLD));
        assertEquals(9, relativeSegmentation.getSegmentEndIndex(0, SIZE_TENFOLD));

        assertEquals(40, relativeSegmentation.getSegmentStartIndex(4, SIZE_TENFOLD));
        assertEquals(49, relativeSegmentation.getSegmentEndIndex(4, SIZE_TENFOLD));

        assertEquals(90, relativeSegmentation.getSegmentStartIndex(9, SIZE_TENFOLD));
        assertEquals(99, relativeSegmentation.getSegmentEndIndex(9, SIZE_TENFOLD));

    }

    @Test
    void getAbsoulteParts() {
        relativeSegmentation.setSegment(0, TEST_COLOR_1);
        relativeSegmentation.setSegment(3, TEST_COLOR_1);
        relativeSegmentation.setSegment(5, TEST_COLOR_2);
        relativeSegmentation.setSegment(9, TEST_COLOR_1);
        relativeSegmentation.setSegment(8, TEST_COLOR_3);
        System.out.println("JAA");
        System.out.println(relativeSegmentation.getSegments(10).stream().map(colorPart -> colorPart.toString()).collect(Collectors.joining(",")));
        System.out.println(relativeSegmentation.getSegments(249).stream().map(colorPart -> colorPart.toString()).collect(Collectors.joining(",")));
        System.out.println(relativeSegmentation.getSegments(100).stream().map(colorPart -> colorPart.toString()).collect(Collectors.joining(",")));
        // System.out.println(partition.getSegments(100).stream().map(colorPart -> colorPart.getColor().toHexString()).collect(Collectors.joining(",")));
    }

}