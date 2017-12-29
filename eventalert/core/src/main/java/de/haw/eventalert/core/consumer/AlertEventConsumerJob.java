package de.haw.eventalert.core.consumer;

import de.haw.eventalert.core.consumer.action.ActionSink;
import de.haw.eventalert.core.consumer.configuration.AlertEventConsumerConfiguration;
import de.haw.eventalert.core.consumer.filter.FilterRule;
import de.haw.eventalert.core.consumer.filter.manager.FilterRuleManager;
import de.haw.eventalert.core.global.alertevent.AlertEvents;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * this flink job collect all {@link de.haw.eventalert.core.global.alertevent.AlertEvent} and filter them by {@link FilterRule}s.
 * if a {@link FilterRule} matches to a {@link de.haw.eventalert.core.global.alertevent.AlertEvent} the {@link FilterRule} with the highest priority is selected in a specific time window
 * and the {@link de.haw.eventalert.core.consumer.action.Action} of the selected {@link FilterRule} will be execute executed by {@link ActionSink}
 */
public class AlertEventConsumerJob {
    private static final Logger LOG = LoggerFactory.getLogger(AlertEventConsumerJob.class);

    public static void main(String[] args) throws Exception {
        LOG.info("========== AlertEventConsumerJob started ==========");

        FilterRuleManager filterRuleManager = AlertEventConsumerConfiguration.filterRuleManager;

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        //Get all alert-events
        DataStream<FilterRule> matchingFilterRuleStream = env.addSource(AlertEventConsumer.createAlertEventConsumer())
                .flatMap(AlertEvents.convertToAlertEvent())
                .filter(AlertEventConsumer.filterAlertEventsWithFilterRules(filterRuleManager))
                .flatMap(AlertEventConsumer.collectMatchingFilters(filterRuleManager));

        AlertEventConsumer.prioritizeFilterRulesInTimeWindow(matchingFilterRuleStream, TumblingProcessingTimeWindows.of(Time.seconds(5)))
                .map(FilterRule::getAction) //get action of filter
                .addSink(new ActionSink()).name("Action Sink"); //execute action

        env.execute();
    }




}
