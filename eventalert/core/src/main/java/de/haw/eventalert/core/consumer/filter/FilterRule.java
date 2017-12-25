package de.haw.eventalert.core.consumer.filter;

import de.haw.eventalert.core.consumer.action.Action;

import java.io.Serializable;

/**
 * Created by Tim on 12.09.2017.
 */
public interface FilterRule extends Serializable {
    String getEventType();

    String getFieldName();

    Condition getCondition();

    Action getAction();

    Integer getPriority(); //TODO muss umgesetzt werden

    boolean equals(Object other);
}
