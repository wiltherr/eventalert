package de.haw.eventalert.core.consumer.filter.manager;

import de.haw.eventalert.core.consumer.filter.FilterRule;
import org.apache.flink.shaded.com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

/**
 * transient implementation of {@link FilterRuleManager} interface
 * <p>
 *     note: no filter rule will be persisted!
 *     its necessary to add all {@link FilterRule}s again after restart the application
 */
public class TransientFilterRuleManager implements FilterRuleManager {

    private static final Logger LOG = LoggerFactory.getLogger(TransientFilterRuleManager.class);

    private Map<String, List<FilterRule>> allFilters;

    public TransientFilterRuleManager() {
        allFilters = new HashMap<>();
    }

    /**
     * adds a {@link FilterRule} to filter this {@link FilterRuleManager}
     *
     * @param filterRule filter rule you want to add
     */
    public void addFilter(FilterRule filterRule) {
        allFilters.computeIfAbsent(filterRule.getEventType(), value -> Lists.newArrayList());
        allFilters.get(filterRule.getEventType()).add(filterRule);
    }

    /**
     * returns all exisitng {@link FilterRule} in {@link TransientFilterRuleManager}
     * <p>
     *     note: use this method only if really needed.
     *     see if you can use {@link #getAllFiltersForEventType(String)} or {@link #getFilters(String, String)} instead
     * @return a list of all existing {@link FilterRule}s
     */
    public List<FilterRule> getAllFilters() {
        List<FilterRule> result = Lists.newArrayList();
        allFilters.values().forEach(result::addAll);
        return result;
    }

    /**
     * @see FilterRuleManager#getAllFiltersForEventType(String)
     */
    @Override
    public List<FilterRule> getAllFiltersForEventType(String eventType) {
        if (!hasFilters(eventType)) {
            LOG.warn("No filters defined for requested eventType '{}'!", eventType);
            return Collections.emptyList();
        }
        return allFilters.get(eventType);
    }

    /**
     * @see FilterRuleManager#getFilters(String, String)
     */
    @Override
    public Stream<FilterRule> getFilters(String eventType, String fieldName) {
        Objects.requireNonNull(fieldName);
        return getAllFiltersForEventType(eventType).stream()
                .filter(filterRule -> fieldName.equals(filterRule.getFieldName()));
    }

    /**
     * @see FilterRuleManager#hasFilters(String)
     */
    @Override
    public boolean hasFilters(String eventType) {
        return allFilters.get(eventType) != null;
    }
}
