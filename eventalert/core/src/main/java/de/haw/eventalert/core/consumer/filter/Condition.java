package de.haw.eventalert.core.consumer.filter;

import de.haw.eventalert.core.consumer.filter.manager.FilterRuleManager;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Condition that has to be fulfilled for a {@link FilterRule} match.
 * is used by {@link de.haw.eventalert.core.consumer.AlertEventConsumer}.
 */
public class Condition implements Serializable {
    private Type type;
    private String pattern;

    /**
     * create a new condition
     *
     * @param type    the type of the condition (see {@link Condition.Type})
     * @param pattern pattern to fulfill the condition (can be regex-pattern, string or number)
     */
    public Condition(Type type, String pattern) {
        this.type = type;
        this.pattern = pattern;
    }

    /**
     * the type of the condition.
     *
     * @return
     */
    public Type getType() {
        return type;
    }

    /**
     * the pattern to fulfill the condition
     *
     * @return
     */
    public String getPattern() {
        return pattern;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(type)
                .append(pattern)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Condition{" +
                "type=" + type +
                ", pattern='" + pattern + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Condition condition = (Condition) o;

        return new EqualsBuilder()
                .append(type, condition.type)
                .append(pattern, condition.pattern)
                .isEquals();
    }

    /**
     * available condition types.
     * are used by {@link de.haw.eventalert.core.consumer.AlertEventConsumer#collectMatchingFilters(FilterRuleManager)}
     */
    public enum Type {
        EQUALS,
        CONTAINS,
        STARTWITH,
        ENDWITH,
        LESS_THAN,
        GREATER_THAN,
        REGEX
    }
}
