package de.haw.eventalert.core.consumer.filter.manager;

import de.haw.eventalert.core.consumer.filter.FilterRule;
import org.apache.flink.shaded.com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Tim on 12.09.2017.
 */
public class TransientFilterRuleManager implements FilterRuleManager {

    private static final Logger LOG = LoggerFactory.getLogger(TransientFilterRuleManager.class);

    private static TransientFilterRuleManager instance;
    private Map<String, List<FilterRule>> allFilters;

    public TransientFilterRuleManager() {
        allFilters = new HashMap<>();
    }

    public void addFilter(FilterRule filterRule) {
        allFilters.computeIfAbsent(filterRule.getEventType(), value -> Lists.newArrayList());
        allFilters.get(filterRule.getEventType()).add(filterRule);
    }

    public List<FilterRule> getAllFilters() {
        List<FilterRule> result = Lists.newArrayList();
        allFilters.values().forEach(result::addAll);
        return result;
    }

    @Override
    public List<FilterRule> getAllFiltersForEventType(String eventType) {
        if (!hasFilters(eventType)) {
            LOG.warn("No filters defined for requested eventType '{}'!", eventType);
            return Collections.emptyList();
        }
        return allFilters.get(eventType);
    }

    @Override
    public Stream<FilterRule> getFilters(String eventType, String filterFieldName) {
        Objects.requireNonNull(filterFieldName);
        return getAllFiltersForEventType(eventType).stream()
                .filter(filterRule -> filterFieldName.equals(filterRule.getFieldName()));
    }

    @Override
    public boolean hasFilters(String eventType) {
        return allFilters.get(eventType) != null;
    }
}
