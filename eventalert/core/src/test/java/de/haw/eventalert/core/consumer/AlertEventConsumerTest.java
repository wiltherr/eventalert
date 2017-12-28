package de.haw.eventalert.core.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.haw.eventalert.core.consumer.filter.Condition;
import de.haw.eventalert.core.consumer.filter.FilterRule;
import de.haw.eventalert.core.consumer.filter.SimpleFilterRule;
import de.haw.eventalert.core.consumer.filter.manager.TransientFilterRuleManager;
import de.haw.eventalert.core.global.alertevent.AlertEvent;
import de.haw.eventalert.core.test.TestEvent;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.apache.flink.shaded.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tim on 26.12.2017.
 */
class AlertEventConsumerTest {

    private static final TestEvent TEST_EVENT_1 = TestEvent.create(1);
    private static final TestEvent TEST_EVENT_2 = TestEvent.create(2);
    private static final TestEvent TEST_EVENT_3 = TestEvent.create(3);

    private static final String TEST_EVENT_TYPE = "testEvent";

    //filterRule matches with all test events
    private static final FilterRule TEST_FILTER_VALID_1 = new SimpleFilterRule(TEST_EVENT_TYPE, "field1", new Condition(Condition.Type.CONTAINS, "Value1"), null, 5);
    //filterRule matches with all test events
    private static final FilterRule TEST_FILTER_VALID_2 = new SimpleFilterRule(TEST_EVENT_TYPE, "field2", new Condition(Condition.Type.ENDWITH, "2"), null, 4);
    //filterRule matches only with test event 3
    private static final FilterRule TEST_FILTER_VALID_3 = new SimpleFilterRule(TEST_EVENT_TYPE, "field3", new Condition(Condition.Type.STARTWITH, "testEvent3"), null, 3);
    //add all valid filters to list
    private static final List<FilterRule> validFilters = Lists.newArrayList(TEST_FILTER_VALID_1, TEST_FILTER_VALID_2, TEST_FILTER_VALID_3);

    //filterRule with unknown event type
    private static final FilterRule TEST_FILTER_INVALID_1 = new SimpleFilterRule("unknownType", "field1", new Condition(Condition.Type.CONTAINS, ""), null, 2);
    //filterRule with wrong condition
    private static final FilterRule TEST_FILTER_INVALID_2 = new SimpleFilterRule(TEST_EVENT_TYPE, "field2", new Condition(Condition.Type.ENDWITH, "3"), null, 1);
    //filterRule with wrong condition
    private static final FilterRule TEST_FILTER_INVALID_3 = new SimpleFilterRule(TEST_EVENT_TYPE, "field3", new Condition(Condition.Type.STARTWITH, "event"), null, 0);
    //add all invalid filters to list
    private static final List<FilterRule> invalidFilters = Lists.newArrayList(TEST_FILTER_INVALID_1, TEST_FILTER_INVALID_2, TEST_FILTER_INVALID_3);

    private TransientFilterRuleManager filterRuleManager;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        FilterRuleSink.collectedEvents.clear();
        AlertEventSink.collectedEvents.clear();
        filterRuleManager = new TransientFilterRuleManager();
    }

    @Test
    void testCollectMatchingFilters() throws Exception {
        //add valid and invalid filters to filterRuleManager
        validFilters.forEach(filterRuleManager::addFilter);
        invalidFilters.forEach(filterRuleManager::addFilter);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        env.fromElements(new AlertEvent(TEST_EVENT_TYPE, TEST_EVENT_1),
                new AlertEvent(TEST_EVENT_TYPE, TEST_EVENT_2),
                new AlertEvent(TEST_EVENT_TYPE, TEST_EVENT_3))

                .flatMap(AlertEventConsumer.collectMatchingFilters(filterRuleManager))
                .addSink(new FilterRuleSink());
        env.execute();

        //assert that collectedList is not empty
        Assertions.assertFalse(FilterRuleSink.collectedEvents.isEmpty());
        //assert that collectedList contains all valid filters
        Assertions.assertTrue(FilterRuleSink.collectedEvents.containsAll(validFilters));
        //assert that collectedList does not contain invalid filters
        Assertions.assertTrue(Collections.disjoint(invalidFilters, FilterRuleSink.collectedEvents));
    }

    @Test
    void testFilterAlertEventsWithFilterRules() throws Exception {
        //add all valid filtersRules to ruleManager
        validFilters.forEach(filterRuleManager::addFilter);

        List<AlertEvent> eventsWithFilterRules = Lists.newArrayList(new AlertEvent(TEST_EVENT_TYPE, TEST_EVENT_2), new AlertEvent(TEST_EVENT_TYPE, TEST_EVENT_3));
        List<AlertEvent> eventsWithoutFilterRules = Lists.newArrayList(new AlertEvent("randomType", TEST_EVENT_1), new AlertEvent("randomType", TEST_EVENT_3));

        List<AlertEvent> testAlertEvents = Lists.newArrayList();
        testAlertEvents.addAll(eventsWithFilterRules);
        testAlertEvents.addAll(eventsWithoutFilterRules);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        env.fromCollection(testAlertEvents)
                .filter(AlertEventConsumer.filterAlertEventsWithFilterRules(filterRuleManager))
                .addSink(new AlertEventSink());
        env.execute();

        //assert that only alertEvents with filter rules are in collectedList
        Assertions.assertIterableEquals(eventsWithFilterRules, AlertEventSink.collectedEvents);
    }

    //TODO this test fails sometimes, if the tumblingProcessingTimeWindow is closed before the event with highest priority is emitted by testSource
    //the test will be repeated up to five times, if any test fail
    @RepeatedIfExceptionsTest(repeats = 5)
    void testPrioritizeFilterRulesInTimeWindow() throws Exception {

        //add valid and invalid filters to filterRuleManager
        validFilters.forEach(filterRuleManager::addFilter);
        invalidFilters.forEach(filterRuleManager::addFilter);

        //get all added filters from ruleManager and shuffle list
        List<FilterRule> testFilterRules = filterRuleManager.getAllFilters();
        Collections.shuffle(testFilterRules);

        //create test source with delay
        SourceFunction<FilterRule> testSource = new FilterRuleSource(1000, 1000, 10, testFilterRules);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        DataStream<FilterRule> filterRuleDataStream = env.addSource(testSource);
        AlertEventConsumer.prioritizeFilterRulesInTimeWindow(filterRuleDataStream, TumblingProcessingTimeWindows.of(Time.milliseconds(1000)))
                .addSink(new FilterRuleSink());
        env.execute();

        //ensure only the filter with highest priority (TEST_FILTER_VALID_1) is in collectedList
        //throw exception instead of assertion test, so the test can be repeated
        if (FilterRuleSink.collectedEvents.size() != 1 || !FilterRuleSink.collectedEvents.get(0).equals(TEST_FILTER_VALID_1)) {
            throw new Exception();
        }
        //Assertions.assertIterableEquals(Lists.newArrayList(), FilterRuleSink.collectedEvents); can be used if test is stable
    }


    private static class FilterRuleSink implements SinkFunction<FilterRule> {
        public static final List<FilterRule> collectedEvents = Lists.newArrayList();

        @Override
        public synchronized void invoke(FilterRule filterRule) throws Exception {
            collectedEvents.add(filterRule);
        }
    }

    private static class AlertEventSink implements SinkFunction<AlertEvent> {
        public static final List<AlertEvent> collectedEvents = Lists.newArrayList();

        @Override
        public synchronized void invoke(AlertEvent alertEvent) throws Exception {
            collectedEvents.add(alertEvent);
        }
    }

    private static class FilterRuleSource implements SourceFunction<FilterRule> {

        private final List<FilterRule> filterRules;
        private final long startDelay;
        private final long shutdownDelay;
        private final long eventDelay;

        public FilterRuleSource(long startDelayInMs, long shutdownDelayInMs, long eventDelayInMs, List<FilterRule> filterRules) {
            this.startDelay = startDelayInMs;
            this.shutdownDelay = shutdownDelayInMs;
            this.eventDelay = eventDelayInMs;
            this.filterRules = filterRules;
        }

        @Override
        public void run(SourceContext<FilterRule> ctx) throws Exception {
            TimeUnit.MILLISECONDS.sleep(startDelay);
            for (FilterRule filterRule : filterRules) {
                ctx.collect(filterRule);
                TimeUnit.MILLISECONDS.sleep(eventDelay);
            }
            TimeUnit.MILLISECONDS.sleep(shutdownDelay);
        }

        @Override
        public void cancel() {
        }
    }
}