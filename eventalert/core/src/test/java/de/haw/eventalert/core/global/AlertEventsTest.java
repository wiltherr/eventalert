package de.haw.eventalert.core.global;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.haw.eventalert.core.global.entity.event.AlertEvent;
import de.haw.eventalert.core.test.TestEvent;
import org.apache.flink.shaded.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class AlertEventsTest {
    private final TestEvent TEST_EVENT = TestEvent.create(0);

    @Test
    void testCreateEvent() throws JsonProcessingException {
        AlertEvent actualAlertEvent = AlertEvents.createEvent(TestEvent.EVENT_TYPE, TEST_EVENT);
        assertEquals(TestEvent.EVENT_TYPE, actualAlertEvent.getEventType());
        assertEquals(TEST_EVENT.getField1(), actualAlertEvent.getEventData().get("field1").asText());
        assertEquals(TEST_EVENT.getField2(), actualAlertEvent.getEventData().get("field2").asText());
        assertEquals(TEST_EVENT.getField3(), actualAlertEvent.getEventData().get("field3").asText());
    }

    @Test
    void testConverting() throws IOException {
        AlertEvent expectedAlertEvent = AlertEvents.createEvent(TestEvent.EVENT_TYPE, TEST_EVENT);
        //convert around
        AlertEvent actualAlertEvent = convertAround(expectedAlertEvent);
        assertEquals(expectedAlertEvent, actualAlertEvent);
    }

    @Test
    void testConvertingStream() throws Exception {
        //Create test event list with 100 events
        List<AlertEvent> exceptedAlertEvents = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            exceptedAlertEvents.add(AlertEvents.createEvent(TestEvent.EVENT_TYPE, TestEvent.create(i)));
        }
        //clear sink
        AlertEventSink.collectedEvents.clear();

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        env.fromCollection(exceptedAlertEvents)
                //convert alertevents to json
                .flatMap(AlertEvents.convertToJSONString())
                //convert json back to alertevents
                .flatMap(AlertEvents.convertToAlertEvent())
                //add to test sink
                .addSink(new AlertEventSink());
        env.execute();
        //assert that all events equals the expected events
        assertIterableEquals(exceptedAlertEvents, AlertEventSink.collectedEvents);
    }

    /**
     * converts a alertEvent to json and back
     *
     * @param alertEvent event that should be converted
     * @return double converted alertEvent
     * @throws IOException if the {@link AlertEvents} conversion failed
     */
    private AlertEvent convertAround(AlertEvent alertEvent) throws IOException {
        return AlertEvents.toAlertEvent(AlertEvents.toJSONString(alertEvent));
    }

    private static class AlertEventSink implements SinkFunction<AlertEvent> {
        public static final List<AlertEvent> collectedEvents = Lists.newArrayList();

        @Override
        public synchronized void invoke(AlertEvent alertEvent) throws Exception {
            collectedEvents.add(alertEvent);
        }
    }

}