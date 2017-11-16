package de.haw.eventalert.core.consumer.filter;

import org.apache.flink.shaded.com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created by Tim on 12.09.2017.
 */
public class FilterManager implements Serializable {

    private static FilterManager instance;
    private Map<String, List<Filter>> allFilters;

    private FilterManager() {
        allFilters = new HashMap<>();
    }

    public static FilterManager getInstance() {
        if (instance == null) {
            instance = new FilterManager();
        }
        return instance;
    }

    public void addFilter(Filter filter) {
        allFilters.computeIfAbsent(filter.getEventType(), value -> Lists.newArrayList(filter));
    }

    public List<Filter> getAllFiltersForEventType(String eventType) throws Exception {
        if (!hasFilters(eventType))
            throw new Exception(String.format("EventType %s is unkown!", eventType));

        return allFilters.get(eventType);
    }

    public Stream<Filter> getFilters(String eventType, String filterFieldName) throws Exception {
        Objects.requireNonNull(filterFieldName); //TODO test
        return getAllFiltersForEventType(eventType).stream()
                .filter(x -> x.getFieldName().equals(filterFieldName));
    }

    public boolean hasFilters(String eventType) {
        return allFilters.get(eventType) != null;
    }
}
