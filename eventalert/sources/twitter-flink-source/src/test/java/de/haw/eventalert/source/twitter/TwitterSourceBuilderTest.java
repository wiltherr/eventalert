package de.haw.eventalert.source.twitter;

import com.twitter.hbc.core.endpoint.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TwitterSourceBuilderTest {

    @Test
    void testLocationConvert() {
        Location expectedLocation = new Location(new Location.Coordinate(9.75, 53.35), new Location.Coordinate(10.24, 53.68));
        //convert around
        Location actualLocation = TwitterSourceBuilder.convertTuple4ToLocation.apply(TwitterSourceBuilder.convertLocationToTuple4.apply(expectedLocation));
        assertEquals(expectedLocation.northeastCoordinate().latitude(), actualLocation.northeastCoordinate().latitude());
        assertEquals(expectedLocation.northeastCoordinate().longitude(), actualLocation.northeastCoordinate().longitude());
        assertEquals(expectedLocation.southwestCoordinate().latitude(), actualLocation.southwestCoordinate().latitude());
        assertEquals(expectedLocation.southwestCoordinate().longitude(), actualLocation.southwestCoordinate().longitude());
    }

}