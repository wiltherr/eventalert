package de.haw.eventalert.ledbridge.entity.color.segmentation;


import de.haw.eventalert.ledbridge.entity.color.Colors;
import de.haw.eventalert.ledbridge.entity.color.types.Color;
import org.ardulink.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class ColorSegmentationTest {


    public static final int CAPACITY_EVEN = 8;
    public static final int CAPACITY_ODD = 11;
    public static final int SIZE_BIGGER = CAPACITY_EVEN + CAPACITY_EVEN / 3 + 1;
    public static final int SIZE_DOUBLE = CAPACITY_EVEN * 2;
    public static final int SIZE_TENFOLD = CAPACITY_EVEN * 10;

    public static final Color TEST_COLOR_1 = Colors.createRGBW(255, 0, 0, 0);
    public static final Color TEST_COLOR_2 = Colors.createRGBW(0, 255, 0, 0);
    public static final Color TEST_COLOR_3 = Colors.createRGBW(0, 0, 255, 0);

    ColorSegmentation colorSegmentationEven;
    ColorSegmentation colorSegmentationOdd;

    private static void checkColorSegment(ColorSegment colorSegment, int capacity) {
        Assertions.assertFalse(colorSegment.getStart() < 0);
        Assertions.assertFalse(colorSegment.getEnd() < 0);
        Assertions.assertFalse(colorSegment.getStart() > colorSegment.getEnd());
        Assertions.assertTrue(colorSegment.getStart() < capacity);
        Assertions.assertTrue(colorSegment.getEnd() < capacity);
    }

    @BeforeEach
    void setUp() {
        colorSegmentationEven = new ColorSegmentation(CAPACITY_EVEN);
        colorSegmentationOdd = new ColorSegmentation(CAPACITY_ODD);
    }

    @Test
    void testCreate() {
        ColorSegmentation colorSegmentation = ColorSegmentation.create(TEST_COLOR_1, TEST_COLOR_2, TEST_COLOR_3);
        Assertions.assertEquals(3, colorSegmentation.getCapacity());
        Assertions.assertEquals(3, colorSegmentation.getSegments().size());
        Assertions.assertIterableEquals(Lists.newArrayList(TEST_COLOR_1, TEST_COLOR_2, TEST_COLOR_3),
                colorSegmentation.getSegments().stream().map(ColorSegment::getColor).collect(Collectors.toList()));
    }


    @Test
    void testGetSegmentsWithOtherCapacity1() {
        ColorSegmentation colorSegmentation50_50 = new ColorSegmentation(2);
        colorSegmentation50_50.setSegment(0, TEST_COLOR_1);
        colorSegmentation50_50.setSegment(1, TEST_COLOR_2);

        ColorSegmentation colorSegmentationBigger = new ColorSegmentation(100);
        List<ColorSegment> colorSegments = colorSegmentation50_50.getSegments(colorSegmentationBigger.getCapacity());
        Assertions.assertEquals(colorSegmentation50_50.getCapacity(), colorSegments.size());

        Assertions.assertEquals(TEST_COLOR_1, colorSegments.get(0).getColor());
        Assertions.assertEquals(0, colorSegments.get(0).getStart());
        Assertions.assertEquals(50, colorSegments.get(0).getEnd());

        Assertions.assertEquals(TEST_COLOR_2, colorSegments.get(1).getColor());
        Assertions.assertEquals(51, colorSegments.get(1).getStart());
        Assertions.assertEquals(99, colorSegments.get(1).getEnd());
    }

    @Test
    void testGetSegmentsWithOtherCapacity2() {
        ColorSegmentation colorSegmentation100 = new ColorSegmentation(100);
        for (int i = 0; i < colorSegmentation100.getCapacity(); i++) {
            int distribution = i % 4;
            if (distribution == 0) {
                colorSegmentation100.setSegment(ColorSegment.create(null, i));
            } else if (distribution == 1) {
                colorSegmentation100.setSegment(ColorSegment.create(TEST_COLOR_1, i));
            } else if (distribution == 2) {
                colorSegmentation100.setSegment(ColorSegment.create(TEST_COLOR_2, i));
            } else if (distribution == 3) {
                colorSegmentation100.setSegment(ColorSegment.create(TEST_COLOR_3, i));
            }

        }

        ColorSegmentation colorSegmentationBigger = new ColorSegmentation(255);
        List<ColorSegment> colorSegments = colorSegmentation100.getSegments(colorSegmentationBigger.getCapacity());
        Assertions.assertEquals(colorSegmentation100.getCapacity(), colorSegments.size());
        colorSegments.forEach(colorSegment -> checkColorSegment(colorSegment, colorSegmentationBigger.getCapacity()));

        //set colorSegments to bigger segmentation
        colorSegments.forEach(colorSegmentationBigger::setSegment);

        //compare both segmentation colors
        List<Color> colors100 = colorSegmentation100.getSegments().stream().map(ColorSegment::getColor).collect(Collectors.toList());
        List<Color> colorsBigger = colorSegmentationBigger.getSegments().stream().map(ColorSegment::getColor).collect(Collectors.toList());
        Assertions.assertEquals(colors100, colorsBigger);

    }

    @Test
    void setSetSegment() {
        Assertions.assertFalse(colorSegmentationEven.setSegment(CAPACITY_EVEN, TEST_COLOR_1));
        Assertions.assertTrue(colorSegmentationEven.setSegment(CAPACITY_EVEN - 1, TEST_COLOR_1));
        Assertions.assertTrue(colorSegmentationEven.setSegment(0, TEST_COLOR_1));
        Assertions.assertTrue(colorSegmentationEven.setSegment(CAPACITY_EVEN / 2, TEST_COLOR_1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> colorSegmentationEven.setSegment(-1, TEST_COLOR_1));

        Assertions.assertFalse(colorSegmentationEven.setSegment(ColorSegment.create(TEST_COLOR_1, 0, CAPACITY_EVEN)));
        Assertions.assertTrue(colorSegmentationEven.setSegment(ColorSegment.create(TEST_COLOR_1, 0, CAPACITY_EVEN - 1)));
        Assertions.assertTrue(colorSegmentationEven.setSegment(ColorSegment.create(TEST_COLOR_1, 0)));
        Assertions.assertTrue(colorSegmentationEven.setSegment(ColorSegment.create(TEST_COLOR_1, CAPACITY_EVEN / 2)));
        Assertions.assertThrows(IllegalArgumentException.class, () -> colorSegmentationEven.setSegment(-1, TEST_COLOR_1));
    }

    @Test
    void testSetAndGetSegment() {
        //odd segmentation capacity
        //ColorSegment 0: empty
        colorSegmentationOdd.setSegment(1, TEST_COLOR_1); //ColorSegment 1-3: TEST_COLOR_1
        colorSegmentationOdd.setSegment(2, TEST_COLOR_1);
        colorSegmentationOdd.setSegment(3, TEST_COLOR_1);
        //ColorSegment 4-5: empty
        colorSegmentationOdd.setSegment(6, TEST_COLOR_2); //ColorSegment 6: TEST_COLOR_2
        colorSegmentationOdd.setSegment(7, TEST_COLOR_3); //ColorSegment 7: TEST_COLOR_3
        colorSegmentationOdd.setSegment(8, TEST_COLOR_1); //ColorSegment 8: TEST_COLOR_1
        colorSegmentationOdd.setSegment(9, TEST_COLOR_2); //ColorSegment 9: TEST_COLOR_2
        //ColorSegment 10: empty

        //expected values
        List<ColorSegment> expectedSegmentsOdd = Arrays.asList(
                ColorSegment.create(null, 0),
                ColorSegment.create(TEST_COLOR_1, 1, 3),
                ColorSegment.create(null, 4, 5),
                ColorSegment.create(TEST_COLOR_2, 6),
                ColorSegment.create(TEST_COLOR_3, 7),
                ColorSegment.create(TEST_COLOR_1, 8),
                ColorSegment.create(TEST_COLOR_2, 9),
                ColorSegment.create(null, 10)
        );

        //even segmentation capacity
        colorSegmentationEven.setSegment(0, TEST_COLOR_1); //ColorSegment 0-3: TEST_COLOR_1
        colorSegmentationEven.setSegment(1, TEST_COLOR_1);
        colorSegmentationEven.setSegment(2, TEST_COLOR_1);
        colorSegmentationEven.setSegment(3, TEST_COLOR_1);
        //SEGMENT 4-5: empty
        colorSegmentationEven.setSegment(6, TEST_COLOR_2); //ColorSegment 6: TEST_COLOR_2
        colorSegmentationEven.setSegment(7, TEST_COLOR_3); //ColorSegment 7: TEST_COLOR_3#

        //expected values
        List<ColorSegment> expectedSegmentsEven = Arrays.asList(
                ColorSegment.create(TEST_COLOR_1, 0, 3),
                ColorSegment.create(null, 4, 5),
                ColorSegment.create(TEST_COLOR_2, 6),
                ColorSegment.create(TEST_COLOR_3, 7)
        );

        //test
        Assertions.assertIterableEquals(expectedSegmentsOdd, colorSegmentationOdd.getSegments());
        Assertions.assertIterableEquals(expectedSegmentsEven, colorSegmentationEven.getSegments());
    }

}