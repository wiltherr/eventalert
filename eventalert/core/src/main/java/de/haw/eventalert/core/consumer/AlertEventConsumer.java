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
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.UUID;

/**
 * Created by Tim on 04.09.2017.
 */
public class AlertEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(AlertEventConsumer.class);

    private static final String KAFKA_BROKER = EventAlertConst.KAFA_BROKER;
    private static final String KAFKA_TOPIC = EventAlertConst.KAFKA_TOPIC_ALERTEVENT;

    public static FlinkKafkaConsumer010<String> createAlertEventConsumer() {
        // set up kafka consumer
        Properties properties = new Properties(); //TODO settings should can be overwritten by calling class
        properties.setProperty("auto.offset.reset", "latest");
//        properties.setProperty("auto.offset.reset", "earliest");
        properties.setProperty("bootstrap.servers", KAFKA_BROKER);
        // random consumer group id
        properties.setProperty("group.id", UUID.randomUUID().toString());

        return new FlinkKafkaConsumer010<>(KAFKA_TOPIC, new SimpleStringSchema(), properties);
    }

    public static FlatMapFunction<AlertEvent, FilterRule> collectMatchingFilters(FilterRuleManager filterRuleManager) {
        return new FlatMapFunction<AlertEvent, FilterRule>() {
            @Override
            public void flatMap(AlertEvent event, Collector<FilterRule> collector) throws Exception {
                event.getEventData().fieldNames().forEachRemaining(fieldName -> { //iterate over existing alertEvent fields
                    filterRuleManager.getFilters(event.getEventType(), fieldName).forEach(filter -> { //iterate over filters matching alertEvent type and fieldName

                        String fieldValue = event.getEventData().get(fieldName).asText(); //TODO here we get all fields as text, no matter if its a date or another field
                        String pattern = filter.getCondition().getPattern();
                        boolean match = false;

                        //check filter condition matching alertEvent filedValue
                        switch (filter.getCondition().getType()) {
                            case CONTAINS:
                                match = fieldValue.contains(pattern);
                                break;
                            case STARTWITH:
                                match = fieldValue.startsWith(pattern);
                                break;
                            case ENDWITH:
                                match = fieldValue.endsWith(pattern);
                                break;
                            case REGEX: //TODO not supported
                                match = fieldValue.matches(pattern);
                                break;
                        }

                        if (match) //collect matching filter
                            collector.collect(filter);
                    });
                });
            }
        };
    }

    public static FilterFunction<AlertEvent> filterAlertEventsWithFilterRules(FilterRuleManager filterRuleManager) {
        return event -> filterRuleManager.hasFilters(event.getEventType());
    }

    public static DataStream<FilterRule> prioritizeFilterRulesInTimeWindow(DataStream<FilterRule> filterRuleStream, WindowAssigner<Object, TimeWindow> windowAssigner) {
        return filterRuleStream.windowAll(windowAssigner)
                .reduce((filterRule1, filterRule2) ->
                        filterRule1.getPriority() > filterRule2.getPriority() ? filterRule1 : filterRule2
                )
                .returns(FilterRule.class).name("Window: prioritize FilterRules");
    }


}
