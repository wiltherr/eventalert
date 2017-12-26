package de.haw.eventalert.core.consumer.filter;

import de.haw.eventalert.core.consumer.filter.manager.TransientFilterRuleManager;
import org.apache.flink.shaded.com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

class TransientFilterRuleManagerTest {

    private static final String PATTERN_STRING = "testPattern";
    private static final String PATTERN_REGEX = ""; //TODO
    private static final String PATTERN_NUMBER_LOW = "1";
    private static final String PATTERN_NUMBER_HIGH = "100";

    private static final Condition CONDITION_CONTAINS = new Condition(Condition.Type.CONTAINS, PATTERN_STRING);
    private static final Condition CONDITION_STARTS = new Condition(Condition.Type.STARTWITH, PATTERN_STRING);
    private static final Condition CONDITION_ENDS = new Condition(Condition.Type.ENDWITH, PATTERN_STRING);
    private static final Condition CONDITION_REGEX = new Condition(Condition.Type.REGEX, PATTERN_REGEX);
    private static final Condition CONDITION_GREATER = new Condition(Condition.Type.GREATER_THAN, PATTERN_NUMBER_LOW);
    private static final Condition CONDITION_LESS = new Condition(Condition.Type.LESS_THAN, PATTERN_NUMBER_HIGH);

    private static final String EVENT_TYPE_NON_EXISITENT = "nonExistentType";
    private static final String EVENT_TYPE_1 = "testType1";
    private static final String EVENT_TYPE_2 = "testType2";
    private static final String EVENT_TYPE_3 = "testType3";

    private static final String EVENT_FILED_1 = "testFiled1";
    private static final String EVENT_FILED_2 = "testFiled2";
    private static final String EVENT_FILED_3 = "testFiled3";

    private static final FilterRule FILTER_RULE_1 = new DefaultFilterRule(EVENT_TYPE_1, EVENT_FILED_1, CONDITION_CONTAINS, new EmptyTestAction(), new Random().nextInt());
    private static final FilterRule FILTER_RULE_2 = new DefaultFilterRule(EVENT_TYPE_2, EVENT_FILED_2, CONDITION_STARTS, new EmptyTestAction(), new Random().nextInt());
    private static final FilterRule FILTER_RULE_3 = new DefaultFilterRule(EVENT_TYPE_3, EVENT_FILED_3, CONDITION_ENDS, new EmptyTestAction(), new Random().nextInt());
    private static final FilterRule FILTER_RULE_4 = new DefaultFilterRule(EVENT_TYPE_1, EVENT_FILED_1, CONDITION_REGEX, new EmptyTestAction(), new Random().nextInt());

    private TransientFilterRuleManager filterRuleManager;

    @BeforeEach
    public void setUp() {
        filterRuleManager = new TransientFilterRuleManager();
    }

    @Test
    public void testAddAndGet() {
        List<FilterRule> expectedFilterRules = Lists.newArrayList();
        addFilterRuleAndCheck(FILTER_RULE_1, expectedFilterRules);
        addFilterRuleAndCheck(FILTER_RULE_2, expectedFilterRules);
        addFilterRuleAndCheck(FILTER_RULE_3, expectedFilterRules);
        addFilterRuleAndCheck(FILTER_RULE_4, expectedFilterRules);

        //group expected filter rules by its eventType
        Map<String, List<FilterRule>> expectedFilterRulesByEventType = expectedFilterRules.stream().collect(Collectors.groupingBy(FilterRule::getEventType));

        //check if all added filterRules are in TransientFilterRuleManager
        expectedFilterRulesByEventType.forEach((eventType, expectedFilterRuleList) ->
                Assertions.assertIterableEquals(expectedFilterRuleList, filterRuleManager.getAllFiltersForEventType(eventType))
        );
    }

    @Test
    public void testGetNonExitent() {
        Assertions.assertFalse(filterRuleManager.hasFilters(EVENT_TYPE_NON_EXISITENT));
        Assertions.assertTrue(filterRuleManager.getAllFiltersForEventType(EVENT_TYPE_NON_EXISITENT).isEmpty());
        Assertions.assertTrue(filterRuleManager.getFilters(EVENT_TYPE_NON_EXISITENT, "").count() == 0);
    }

    private void addFilterRuleAndCheck(FilterRule filterRule, List<FilterRule> expectedList) {
        filterRuleManager.addFilter(filterRule);
        //check if manager has filter rules with event type
        Assertions.assertTrue(filterRuleManager.hasFilters(filterRule.getEventType()));
        //check if manager contains added filter rule
        Assertions.assertTrue(filterRuleManager
                .getFilters(filterRule.getEventType(), filterRule.getFieldName())
                .anyMatch(actualFilterRule -> Objects.equals(actualFilterRule, filterRule))
        );
        expectedList.add(filterRule);
    }
}