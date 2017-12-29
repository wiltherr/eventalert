package de.haw.eventalert.core.consumer;

import de.haw.eventalert.core.consumer.filter.FilterRule;
import de.haw.eventalert.core.consumer.filter.manager.FilterRuleManager;
import de.haw.eventalert.core.global.EventAlertConst;
import de.haw.eventalert.core.global.alertevent.AlertEvent;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

import java.util.Properties;
import java.util.UUID;

/**
 * Util class for consuming and filtering {@link AlertEvent} streams of Kafka.
 * Uses a {@link FilterRuleManager} for filtering events.
 */
public class AlertEventConsumer {

    private static final String KAFKA_BROKER = EventAlertConst.KAFA_BROKER;
    private static final String KAFKA_TOPIC = EventAlertConst.KAFKA_TOPIC_ALERTEVENT;

    /**
     * creates a kafka consumer that collect all {@link AlertEvent}s of EventAlert-topic
     * <p>this method is used to collect AlertEvents of Kafka in a dataStream for flink
     *
     * @return a new {@link org.apache.flink.streaming.api.functions.source.SourceFunction}
     * @see AlertEventConsumerJob for usage example
     */
    public static FlinkKafkaConsumer010<String> createAlertEventConsumer() {
        // set up kafka consumer
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", KAFKA_BROKER);
        // only get the newest events
        properties.setProperty("auto.offset.reset", "latest");
        // random consumer group id, so the consumer will get only the newest events
        properties.setProperty("group.id", UUID.randomUUID().toString());

        return new FlinkKafkaConsumer010<>(KAFKA_TOPIC, new SimpleStringSchema(), properties);
    }

    /**
     * {@link FlatMapFunction} that check if filterRules of {@link FilterRuleManager} matching with {@link AlertEvent} in dataStream
     * will return the {@link FilterRule} if a match was detected.
     *
     * @param filterRuleManager manager with defined FilterRules
     * @return a {@link FlatMapFunction}
     */
    public static FlatMapFunction<AlertEvent, FilterRule> collectMatchingFilters(FilterRuleManager filterRuleManager) {
        return (FlatMapFunction<AlertEvent, FilterRule>) (event, collector) ->
                //iterate over existing alertEvent fields
                event.getEventData().fieldNames().forEachRemaining(fieldName -> {
                    //iterate over filters matching alertEvent type and fieldName
                    filterRuleManager.getFilters(event.getEventType(), fieldName).forEach(filter -> {

                        String fieldValue = event.getEventData().get(fieldName).asText(); //TODO here we get all fields as text, no matter if its a date or another field
                        String pattern = filter.getCondition().getPattern();
                        boolean match = false;

                        //check filter condition matching alertEvent filedValue
                        switch (filter.getCondition().getType()) {
                            case EQUALS:
                                match = fieldValue.equals(pattern);
                                break;
                            case CONTAINS:
                                match = fieldValue.contains(pattern);
                                break;
                            case STARTWITH:
                                match = fieldValue.startsWith(pattern);
                                break;
                            case ENDWITH:
                                match = fieldValue.endsWith(pattern);
                                break;
                            case REGEX:
                                match = fieldValue.matches(pattern);
                                break;
                            case LESS_THAN:
                                match = Integer.valueOf(fieldValue) < Integer.valueOf(pattern);
                                break;
                            case GREATER_THAN:
                                match = Integer.valueOf(fieldValue) > Integer.valueOf(pattern);
                                break;
                        }

                        if (match) //collect matching filter
                            collector.collect(filter);
                    });
                });
    }

    /**
     * {@link FilterFunction} that filters out {@link AlertEvent} with unknown eventTyp in {@link FilterRuleManager}
     * that means, if no {@link FilterRule} is defined for {@link AlertEvent#eventType} the event rejected
     *
     * @param filterRuleManager manager with defined FilterRules
     * @return a {@link FilterFunction}
     */
    public static FilterFunction<AlertEvent> filterAlertEventsWithFilterRules(FilterRuleManager filterRuleManager) {
        return event -> filterRuleManager.hasFilters(event.getEventType());
    }

    /**
     * this method will prioritize a stream of {@link FilterRule} by its priority value ({@link FilterRule#getPriority()}) in a given window.
     * at maximum one {@link FilterRule} will be returned after the window ends.
     * <p>
     * note: if two or more {@link FilterRule}s have all the same and highest filter rule, only the first received {@link FilterRule} will be emitted.
     *
     * @param filterRuleStream stream of {@link FilterRule}
     * @param windowAssigner   time window for prioritizing (you can only use windows of processing time)
     * @return a stream of {@link FilterRule} with the highest priority in the given windowAssigner. the stream contains one filterRule per window at maximum.
     */
    public static DataStream<FilterRule> prioritizeFilterRulesInTimeWindow(DataStream<FilterRule> filterRuleStream, WindowAssigner<Object, TimeWindow> windowAssigner) {
        return filterRuleStream.windowAll(windowAssigner)
                .reduce((filterRule1, filterRule2) ->
                        filterRule1.getPriority() > filterRule2.getPriority() ? filterRule1 : filterRule2
                )
                .returns(FilterRule.class).name("Window: prioritize FilterRules");
    }


}
