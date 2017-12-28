package de.haw.eventalert.core.consumer.filter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by Tim on 13.09.2017.
 */
public class Condition implements Serializable {
    private Type type;

    private String pattern;

    public Condition(Type type, String pattern) {
        this.type = type;
        this.pattern = pattern;
    }

    public Type getType() {
        return type;
    }

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

    public enum Type {
        CONTAINS,
        STARTWITH,
        ENDWITH,
        LESS_THAN,
        GREATER_THAN,
        REGEX
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
}
