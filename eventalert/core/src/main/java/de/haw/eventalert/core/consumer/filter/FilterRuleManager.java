package de.haw.eventalert.core.consumer.filter;

import org.apache.flink.shaded.com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Tim on 12.09.2017.
 */
public class FilterRuleManager implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(FilterRuleManager.class);

    private static FilterRuleManager instance;
    private Map<String, List<FilterRule>> allFilters;

    private FilterRuleManager() {
        allFilters = new HashMap<>();
    }

    public static FilterRuleManager getInstance() {
        if (instance == null) {
            instance = new FilterRuleManager();
        }
        return instance;
    }

    public void addFilter(FilterRule filterRule) {
        allFilters.computeIfAbsent(filterRule.getEventType(), value -> Lists.newArrayList());
        allFilters.get(filterRule.getEventType()).add(filterRule);
    }

    public List<FilterRule> getAllFiltersForEventType(String eventType) {
        if (!hasFilters(eventType)) {
            LOG.warn("No filters defined for requested eventType '{}'!", eventType);
            return Collections.emptyList();
        }
        return allFilters.get(eventType);
    }

    public Stream<FilterRule> getFilters(String eventType, String filterFieldName) {
        Objects.requireNonNull(filterFieldName); //TODO test
        return getAllFiltersForEventType(eventType).stream()
                .filter(filterRule -> filterFieldName.equals(filterRule.getFieldName()));
    }

    public boolean hasFilters(String eventType) {
        return allFilters.get(eventType) != null;
    }
}
