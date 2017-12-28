package de.haw.eventalert.core.consumer.action;

import java.io.Serializable;

/**
 * this interface is implemented by all Actions, that are executed by {@link ActionSink}.
 * {@link Action}s can be added to {@link de.haw.eventalert.core.consumer.filter.FilterRule}s.
 * @see de.haw.eventalert.core.consumer.action.example.ExampleAction for a example implementation
 */
public interface Action extends Serializable {
    /**
     * name of the action
     *
     * @return string with the name of the action
     */
    String getName();

    /**
     * this method is executed by {@link ActionSink}
     * @throws Exception
     */
    void runAction() throws Exception;

    /**
     * this method should return a string, which is logged by {@link ActionSink} before executing the Action.
     * must be not set (optional)
     * @return configuration of the action
     */
    String getConfigurationForLog();
}
