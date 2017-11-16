package de.haw.eventalert.ledbridge.core.util;


import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
    public static final ObjectMapper jsonMapper;

    static {
        ObjectMapper mapper = new ObjectMapper();
        jsonMapper = mapper;
    }
}
