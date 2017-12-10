package de.haw.eventalert.core.consumer.action;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Tim on 01.11.2017.
 */
public class ActionSink implements SinkFunction<Action> {
    private static final Logger LOG = LoggerFactory.getLogger(ActionSink.class);

    @Override
    public void invoke(Action action) throws Exception {
        try {
            LOG.info("Running action {} with configuration {}", action.getName(), action.getConfigurationForLog());
            action.runAction();
        } catch (Exception e) {
            LOG.error("Running action {} failed", action.getName(), e);
        }
    }
}