package de.haw.eventalert.core.consumer;

import de.haw.eventalert.core.consumer.action.ActionSink;
import de.haw.eventalert.core.consumer.filter.FilterRule;
import de.haw.eventalert.core.consumer.filter.manager.TransientFilterRuleManager;
import de.haw.eventalert.core.global.AlertEvents;
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

    private static final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);

    public static void main(String[] args) throws Exception {
        LOG.info("========== AlertEventConsumerJob started ==========");

        //Get all alert-events
        DataStream<String> jsonAlertEventStream = env.addSource(AlertEventConsumer.createAlertEventConsumer());


        //FilterRule Events

        //Tuple4<FilterParamName, contains/startWith/endWith/regex/greaterThan/lowerThan/equals, filter, LEDEffect>
        //Init filters
        TransientFilterRuleManager filterRuleManager = new TransientFilterRuleManager();
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

        DataStream<FilterRule> matchingFilterRuleStream = env.addSource(AlertEventConsumer.createAlertEventConsumer())
                .flatMap(AlertEvents.convertToAlertEvent())
                .filter(AlertEventConsumer.filterAlertEventsWithFilterRules(filterRuleManager))
                .flatMap(AlertEventConsumer.collectMatchingFilters(filterRuleManager));

        AlertEventConsumer.priorisizeFilterRulesInTimeWindow(matchingFilterRuleStream, TumblingProcessingTimeWindows.of(Time.seconds(5)))
                .map(FilterRule::getAction) //get action of filter
                .addSink(new ActionSink()).name("Action Sink"); //execute action

        env.execute();
    }




}
