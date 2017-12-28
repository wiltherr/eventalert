package de.haw.eventalert.source.twitter;

import com.fasterxml.jackson.databind.JsonNode;

import java.text.DateFormat;
import java.text.ParseException;

/**
 * Created by Tim on 28.12.2017.
 */
public class Tweet {

    private static String JSON_FIELD_TEXT = "text";
    private static String JSON_FIELD_CREATED = "created_at";
    private String text;
    private long createdTime;

    private Tweet() {
    }

    public static boolean isValid(JsonNode jsonNode) {
        return jsonNode.has(JSON_FIELD_TEXT) && jsonNode.has(JSON_FIELD_CREATED);
    }

    public static Tweet fromJsonNode(JsonNode jsonNode) {
        Tweet tweet = new Tweet();
        tweet.setText(jsonNode.get(JSON_FIELD_TEXT).asText());
        try {
            tweet.setCreatedTime(DateFormat.getInstance().parse(jsonNode.get(JSON_FIELD_CREATED).asText()).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return tweet;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
}
