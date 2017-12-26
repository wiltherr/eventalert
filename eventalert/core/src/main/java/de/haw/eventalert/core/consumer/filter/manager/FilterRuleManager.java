package de.haw.eventalert.core.consumer.filter.manager;

import de.haw.eventalert.core.consumer.filter.FilterRule;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Tim on 26.12.2017.
 */
public interface FilterRuleManager extends Serializable {
    List<FilterRule> getAllFiltersForEventType(String eventType);

    Stream<FilterRule> getFilters(String eventType, String filterFieldName);

    boolean hasFilters(String eventType);
}
