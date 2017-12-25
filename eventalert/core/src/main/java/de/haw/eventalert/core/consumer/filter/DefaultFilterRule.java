package de.haw.eventalert.core.consumer.filter;

import de.haw.eventalert.core.consumer.action.Action;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by Tim on 12.09.2017.
 */
public class DefaultFilterRule implements FilterRule {

    private String eventType;
    private String fieldName;
    private Condition condition;
    private Action action;
    private Integer priority;

    public DefaultFilterRule(String eventType, String fieldName, Condition condition, Action action, Integer priority) {
        this.eventType = eventType;
        this.fieldName = fieldName;
        this.condition = condition;
        this.action = action;
        this.priority = priority;
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public Condition getCondition() {
        return condition;
    }

    @Override
    public Action getAction() {
        return action;
    }

    @Override
    public Integer getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DefaultFilterRule that = (DefaultFilterRule) o;

        return new EqualsBuilder()
                .append(eventType, that.eventType)
                .append(fieldName, that.fieldName)
                .append(condition, that.condition)
                .append(action, that.action)
                .append(priority, that.priority)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(eventType)
                .append(fieldName)
                .append(condition)
                .append(action)
                .append(priority)
                .toHashCode();
    }
}
