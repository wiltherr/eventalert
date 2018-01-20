package de.haw.eventalert.ledbridge.entity.event;

import de.haw.eventalert.ledbridge.entity.color.Colors;
import de.haw.eventalert.ledbridge.entity.color.types.Color;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class ColorSegmentationTest {


    public static final int CAPACITY_EVEN = 8;
    public static final int CAPACITY_ODD = 9;
    public static final int SIZE_BIGGER = CAPACITY_EVEN + CAPACITY_EVEN / 3 + 1;
    public static final int SIZE_DOUBLE = CAPACITY_EVEN * 2;
    public static final int SIZE_TENFOLD = CAPACITY_EVEN * 10;

    public static final Color TEST_COLOR_1 = Colors.createRGBW(255, 0, 0, 0);
    public static final Color TEST_COLOR_2 = Colors.createRGBW(0, 255, 0, 0);
    public static final Color TEST_COLOR_3 = Colors.createRGBW(0, 0, 255, 0);

    ColorSegmentation colorSegmentationEven;
    ColorSegmentation colorSegmentationOdd;

    @BeforeEach
    void setUp() {
        colorSegmentationEven = new ColorSegmentation(CAPACITY_EVEN);
        colorSegmentationOdd = new ColorSegmentation(CAPACITY_ODD);
    }

    void printSegmentation(ColorSegmentation colorSegmentation, int absSize) {
        for (int i = 0; i < colorSegmentation.getCapacity(); i++) {
            int start = colorSegmentation.calcSegmentStartIndex(i, absSize);
            int end = colorSegmentation.calcSegmentEndIndex(i, absSize);
            System.out.printf("%d: %d/%d (%d)%n", i, start, end, (end - start) + 1);
        }
    }

    @Test
    void calculateAbsoultePart() {

    }

    @Test
    void getAbsoulteParts() {

        colorSegmentationEven.setSegment(0, TEST_COLOR_1);
        colorSegmentationEven.setSegment(3, TEST_COLOR_1);
        colorSegmentationEven.setSegment(5, TEST_COLOR_2);
        colorSegmentationEven.setSegment(9, TEST_COLOR_1);
        colorSegmentationEven.setSegment(8, TEST_COLOR_3);
        System.out.println("JAA");
        System.out.println(colorSegmentationEven.getSegments(10).stream().map(colorPart -> colorPart.toString()).collect(Collectors.joining(",")));
        System.out.println(colorSegmentationEven.getSegments(249).stream().map(colorPart -> colorPart.toString()).collect(Collectors.joining(",")));
        System.out.println(colorSegmentationEven.getSegments(100).stream().map(colorPart -> colorPart.toString()).collect(Collectors.joining(",")));
        // System.out.println(partition.getSegments(100).stream().map(colorPart -> colorPart.getColor().toHexString()).collect(Collectors.joining(",")));
    }

    @Test
    void setSetSegment() {
        Assertions.assertFalse(colorSegmentationEven.setSegment(CAPACITY_EVEN, TEST_COLOR_1));
        Assertions.assertTrue(colorSegmentationEven.setSegment(CAPACITY_EVEN - 1, TEST_COLOR_1));
        Assertions.assertTrue(colorSegmentationEven.setSegment(0, TEST_COLOR_1));
        Assertions.assertTrue(colorSegmentationEven.setSegment(CAPACITY_EVEN / 2, TEST_COLOR_1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> colorSegmentationEven.setSegment(-1, TEST_COLOR_1));
    }

    @Test
    void testGetSegments() {

        colorSegmentationEven.setSegment(0, TEST_COLOR_1); //ColorSegment 0-3: TEST_COLOR_1
        colorSegmentationEven.setSegment(1, TEST_COLOR_1);
        colorSegmentationEven.setSegment(2, TEST_COLOR_1);
        colorSegmentationEven.setSegment(3, TEST_COLOR_1);
        //SEGMENT 4-5: empty
        colorSegmentationEven.setSegment(6, TEST_COLOR_2); //ColorSegment 6: TEST_COLOR_2
        colorSegmentationEven.setSegment(7, TEST_COLOR_3); //ColorSegment 7: TEST_COLOR_3
        colorSegmentationEven.setSegment(8, TEST_COLOR_1); //ColorSegment 8: TEST_COLOR_1
        List<ColorSegment> expectedSegmentsEven = Arrays.asList(
                ColorSegment.create(TEST_COLOR_1, 0, 3),
                ColorSegment.create(null, 4, 5),
                ColorSegment.create(TEST_COLOR_2, 6),
                ColorSegment.create(TEST_COLOR_3, 7),
                ColorSegment.create(TEST_COLOR_1, 8)
        );


        colorSegmentationOdd.setSegment(0, TEST_COLOR_1); //ColorSegment 0-2: TEST_COLOR_1
        colorSegmentationOdd.setSegment(1, TEST_COLOR_1);
        colorSegmentationOdd.setSegment(2, TEST_COLOR_1);
        colorSegmentationOdd.setSegment(3, TEST_COLOR_1);
        //SEGMENT 4-5: empty
        colorSegmentationOdd.setSegment(6, TEST_COLOR_2); //ColorSegment 6: TEST_COLOR_2
        colorSegmentationOdd.setSegment(7, TEST_COLOR_3); //ColorSegment 8: TEST_COLOR_3
        colorSegmentationOdd.setSegment(8, TEST_COLOR_1); //ColorSegment 8: TEST_COLOR_1
        colorSegmentationOdd.setSegment(9, TEST_COLOR_2); //ColorSegment 9: TEST_COLOR_2

        List<ColorSegment> expectedSegementsOdd = new ArrayList<>();


        System.out.println(colorSegmentationEven.getSegments().stream().map(colorPart -> colorPart.toString()).collect(Collectors.joining(",")));
        System.out.println();
    }

}