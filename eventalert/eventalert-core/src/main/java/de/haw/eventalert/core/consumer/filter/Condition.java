package de.haw.eventalert.core.consumer.filter;

import java.io.Serializable;

/**
 * Created by Tim on 13.09.2017.
 */
public class Condition implements Serializable {
    private Type type;
    private String pattern; //TODO umbennen?

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

    public enum Type {
        CONTAINS,
        STARTWITH,
        ENDWITH,
        REGEX
    }
}
