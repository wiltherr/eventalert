package de.haw.eventalert.core.consumer.filter;

import de.haw.eventalert.core.consumer.action.Action;

/**
 * Created by Tim on 12.09.2017.
 */
public class DefaultFilter implements Filter {

    private String eventType;
    private String fieldName;
    private Condition condition;
    private Action action;

    public DefaultFilter(String eventType, String fieldName, Condition condition, Action action) {
        this.eventType = eventType;
        this.fieldName = fieldName;
        this.condition = condition;
        this.action = action;
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
}
