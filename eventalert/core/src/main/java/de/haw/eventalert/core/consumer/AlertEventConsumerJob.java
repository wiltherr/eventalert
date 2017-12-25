package de.haw.eventalert.core.consumer;

import de.haw.eventalert.core.consumer.action.ActionSink;
import de.haw.eventalert.core.consumer.filter.FilterRule;
import de.haw.eventalert.core.consumer.filter.FilterRuleManager;
import de.haw.eventalert.core.global.AlertEvents;
import de.haw.eventalert.core.global.entity.event.AlertEvent;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Tim on 19.08.2017.
 */
public class AlertEventConsumerJob {
    private static final Logger LOG = LoggerFactory.getLogger(AlertEventConsumerJob.class);

    public static void main(String[] args) throws Exception {
        LOG.info("========== AlertEventConsumerJob started ==========");
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        //Get all alert-events
        DataStream<String> jsonAlertEventStream = env.addSource(AlertEventConsumer.createAlertEventConsumer());
        //convert jsonAlertEvents to AlertEvent Objects
        DataStream<AlertEvent> alertEventStream = jsonAlertEventStream
                .flatMap(
                        AlertEvents.convertToAlertEvent()
                );

        //FilterRule Events

        //Tuple4<FilterParamName, contains/startWith/endWith/regex/greaterThan/lowerThan/equals, filter, LEDEffect>
        //Init filters
        FilterRuleManager filterRuleManager = FilterRuleManager.getInstance();
        // TimedColorEvent testLEDEvent = new TimedColorEvent();
        // testLEDEvent.setColor(Colors.createRGBW(0, 255, 255, 0));
        // testLEDEvent.setBrightness(Brightness.MAX);
        // testLEDEvent.setDuration(125);
        // testLEDEvent.setTargetLEDId(0);

        //TimedColorEvent testLEDEventZwei = testLEDEvent.;
        //testLEDEventZwei.setTargetLEDId(1);
        //filterRuleManager.addFilter(new DefaultFilterRule(MailMessage.EVENT_TYPE, "from", new Condition(CONTAINS, "tim@ksit.org"), Actions.createLEDEventAction(testLEDEvent)));
        //filterRuleManager.addFilter(new DefaultFilterRule(MailMessage.EVENT_TYPE, "from", new Condition(CONTAINS, "tim@ksit.org"), Actions.createLEDEventAction(testLEDEventZwei), true));
        //filterRuleManager.addFilter(new DefaultFilterRule(MailMessage.EVENT_TYPE, "from", new Condition(CONTAINS, "tim@ksit.org"), Actions.createLEDEventAction(testLEDEvent)));
        //filterRuleManager.addFilter(new DefaultFilterRule(MailMessage.EVENT_TYPE,"to", new Condition(STARTWITH, "wittler"), Actions.createLEDEventAction("LED Leuchtet rot")));

        transformToMatchingFilterRules(alertEventStream, filterRuleManager) //get filters matching event
                .windowAll(TumblingProcessingTimeWindows.of(Time.seconds(5))) //check matching filters every 5 seconds
                .max("priority") //get filter with max priority
                .map(FilterRule::getAction) //get action of filter
                .addSink(new ActionSink()).name("Action Sink"); //execute action

        env.execute();
    }

    public static DataStream<FilterRule> transformToMatchingFilterRules(DataStream<AlertEvent> alertEventStream, final FilterRuleManager filterRuleManager) {
        return alertEventStream.filter(event -> filterRuleManager.hasFilters(event.getEventType())) //filter after alertEvent type
                .flatMap((event, collector) -> //collect matching filters for event
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
                        }));
    }
}
