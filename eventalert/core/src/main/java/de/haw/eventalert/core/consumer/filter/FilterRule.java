package de.haw.eventalert.core.consumer.filter;

import de.haw.eventalert.core.consumer.action.Action;

import java.io.Serializable;

/**
 * {@link FilterRule} interface is used to filter {@link de.haw.eventalert.core.global.alertevent.AlertEvent}s
 * FilerRules are managed by a {@link de.haw.eventalert.core.consumer.filter.manager.FilterRuleManager}
 * which is used in {@link de.haw.eventalert.core.consumer.AlertEventConsumer}
 */
public interface FilterRule extends Serializable {
    /**
     * get the original event type, which the {@link FilterRule} is for.
     *
     * @return original event type
     */
    String getEventType();

    /**
     * get field name, which the {@link FilterRule} is for.
     * @return the filed name
     */
    String getFieldName();

    /**
     * get the condition, which should be fulfilled
     * @return a {@link Condition}
     */
    Condition getCondition();

    /**
     * get the action that should be executed if the {@link FilterRule} matches
     * @return a {@link Action}
     */
    Action getAction();

    /**
     * get the priority of the {@link FilterRule}. can be used to reduce FilerRules
     * @return integer that indicates the priority of the {@link FilterRule}
     */
    int getPriority();

    boolean equals(Object other);
}
