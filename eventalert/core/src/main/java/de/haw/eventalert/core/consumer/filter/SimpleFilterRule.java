package de.haw.eventalert.core.consumer.filter;

import de.haw.eventalert.core.consumer.action.Action;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * simple implementation of {@link FilterRule} interface
 * @see FilterRule
 */
public class SimpleFilterRule implements FilterRule {

    private String eventType;
    private String fieldName;
    private Condition condition;
    private Action action;
    private int priority;

    public SimpleFilterRule(String eventType, String fieldName, Condition condition, Action action, Integer priority) {
        this.eventType = eventType;
        this.fieldName = fieldName;
        this.condition = condition;
        this.action = action;
        this.priority = priority;
    }

    /**
     * @see FilterRule#getEventType()
     */
    @Override
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * @see FilterRule#getFieldName()
     */
    @Override
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @see FilterRule#getCondition()
     */
    @Override
    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    /**
     * @see FilterRule#getAction()
     */
    @Override
    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    /**
     * @see FilterRule#getPriority()
     */
    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SimpleFilterRule that = (SimpleFilterRule) o;

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

    @Override
    public String toString() {
        return "SimpleFilterRule{" +
                "eventType='" + eventType + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", condition=" + condition +
                ", action=" + action +
                ", priority=" + priority +
                '}';
    }
}
