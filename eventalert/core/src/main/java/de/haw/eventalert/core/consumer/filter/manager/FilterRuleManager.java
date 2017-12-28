package de.haw.eventalert.core.consumer.filter.manager;

import de.haw.eventalert.core.consumer.filter.FilterRule;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

/**
 * interface for a FilterRuleManager.
 * the {@link FilterRuleManager} manages {@link FilterRule}s for filtering {@link de.haw.eventalert.core.global.alertevent.AlertEvent}s.
 * Manager is used in {@link de.haw.eventalert.core.consumer.AlertEventConsumer}
 */
public interface FilterRuleManager extends Serializable {
    /**
     * get all {@link FilterRule}s for a specific eventType
     *
     * @param eventType name of the original event type
     * @return a list with {@link FilterRule}s or an empty List if no {@link FilterRule} was found
     */
    List<FilterRule> getAllFiltersForEventType(String eventType);

    /**
     * get {@link FilterRule}s for a specific eventType and fieldName.
     * returns all {@link FilterRule}s that are matching with eventType and fieldName
     *
     * @param eventType name of the original event type
     * @param fieldName field name of original event
     * @return {@link Stream} of FilterRules that are matching the eventType and fieldName
     */
    Stream<FilterRule> getFilters(String eventType, String fieldName);

    /**
     * check if any filter exists for given eventType
     * can be used to prefilter {@link de.haw.eventalert.core.global.alertevent.AlertEvent}s
     *
     * @param eventType name of the original event type
     * @return true if filter were found
     */
    boolean hasFilters(String eventType);
}
