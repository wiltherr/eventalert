package de.haw.eventalert.ledbridge.entity.event;

import de.haw.eventalert.ledbridge.entity.color.Colors;
import de.haw.eventalert.ledbridge.entity.color.types.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RelativePartitionTest {


    public static final int SIZE = 10;
    public static final int SIZE_BIGGER = SIZE + 1;
    public static final int SIZE_DOUBLE = SIZE * 2;
    public static final int SIZE_TENFOLD = SIZE * 10;

    public static final Color TEST_COLOR_1 = Colors.createRGBW(255, 0, 0, 0);
    public static final Color TEST_COLOR_2 = Colors.createRGBW(0, 255, 0, 0);
    public static final Color TEST_COLOR_3 = Colors.createRGBW(0, 0, 255, 0);

    RelativePartition partitionSmall;

    @BeforeEach
    void setUp() {
        partitionSmall = new RelativePartition(SIZE);
    }

    @Test
    void calculateAbsoultePart() {
        //same absolute size
        assertEquals(0, partitionSmall.calculateAbsoultePartStart(0, SIZE));
        assertEquals(0, partitionSmall.calculateAbsoultePartEnd(0, SIZE));

        assertEquals(4, partitionSmall.calculateAbsoultePartStart(4, SIZE));
        assertEquals(4, partitionSmall.calculateAbsoultePartEnd(4, SIZE));

        assertEquals(9, partitionSmall.calculateAbsoultePartStart(9, SIZE));
        assertEquals(9, partitionSmall.calculateAbsoultePartEnd(9, SIZE));

        //double size bigger
//        assertEquals(0, partitionSmall.calculateAbsoultePartStart(0,SIZE_DOUBLE));
//        assertEquals(1, partitionSmall.calculateAbsoultePartEnd(0,SIZE_DOUBLE));
//
//        assertEquals(2, partitionSmall.calculateAbsoultePartStart(1,SIZE_DOUBLE));
//        assertEquals(3, partitionSmall.calculateAbsoultePartEnd(1,SIZE_DOUBLE));
//
//        assertEquals(4, partitionSmall.calculateAbsoultePartStart(4,SIZE_DOUBLE));
//        assertEquals(5, partitionSmall.calculateAbsoultePartEnd(4,SIZE_DOUBLE));
//
//        assertEquals(9, partitionSmall.calculateAbsoultePartStart(9,SIZE_DOUBLE));
//        assertEquals(10, partitionSmall.calculateAbsoultePartEnd(9,SIZE_DOUBLE));

        //double absolute size
        assertEquals(0, partitionSmall.calculateAbsoultePartStart(0, SIZE_DOUBLE));
        assertEquals(1, partitionSmall.calculateAbsoultePartEnd(0, SIZE_DOUBLE));

        assertEquals(8, partitionSmall.calculateAbsoultePartStart(4, SIZE_DOUBLE));
        assertEquals(9, partitionSmall.calculateAbsoultePartEnd(4, SIZE_DOUBLE));

        assertEquals(18, partitionSmall.calculateAbsoultePartStart(9, SIZE_DOUBLE));
        assertEquals(19, partitionSmall.calculateAbsoultePartEnd(9, SIZE_DOUBLE));


        //tenfold absolute size
        assertEquals(0, partitionSmall.calculateAbsoultePartStart(0, SIZE_TENFOLD));
        assertEquals(9, partitionSmall.calculateAbsoultePartEnd(0, SIZE_TENFOLD));

        assertEquals(40, partitionSmall.calculateAbsoultePartStart(4, SIZE_TENFOLD));
        assertEquals(49, partitionSmall.calculateAbsoultePartEnd(4, SIZE_TENFOLD));

        assertEquals(90, partitionSmall.calculateAbsoultePartStart(9, SIZE_TENFOLD));
        assertEquals(99, partitionSmall.calculateAbsoultePartEnd(9, SIZE_TENFOLD));

    }

    @Test
    void getAbsoulteParts() {
        partitionSmall.setPart(0, TEST_COLOR_1);
        partitionSmall.setPart(3, TEST_COLOR_1);
        partitionSmall.setPart(5, TEST_COLOR_2);
        partitionSmall.setPart(9, TEST_COLOR_1);
        partitionSmall.setPart(8, TEST_COLOR_3);
        System.out.println("JAA");
        System.out.println(partitionSmall.getAbsoulteParts(10).stream().map(colorPart -> colorPart.toString()).collect(Collectors.joining(",")));
        System.out.println(partitionSmall.getAbsoulteParts(249).stream().map(colorPart -> colorPart.toString()).collect(Collectors.joining(",")));
        System.out.println(partitionSmall.getAbsoulteParts(100).stream().map(colorPart -> colorPart.toString()).collect(Collectors.joining(",")));
        // System.out.println(partition.getAbsoulteParts(100).stream().map(colorPart -> colorPart.getColor().toHexString()).collect(Collectors.joining(",")));
    }

}