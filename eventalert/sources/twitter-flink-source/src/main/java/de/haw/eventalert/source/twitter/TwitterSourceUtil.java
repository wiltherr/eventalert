package de.haw.eventalert.source.twitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.streaming.api.datastream.DataStream;

/**
 * Created by Tim on 28.12.2017.
 */
public class TwitterSourceUtil {
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    public static DataStream<JsonNode> convertJson(DataStream<String> jsonStringTweetStream) {
        return jsonStringTweetStream.filter(StringUtils::isNotBlank).map(jsonTweet -> jsonMapper.readValue(jsonTweet, JsonNode.class));
    }

    public static DataStream<Tweet> convertToTweet(DataStream<String> jsonStringTweetStream) {
        return convertJson(jsonStringTweetStream).filter(Tweet::isValid).map(Tweet::fromJsonNode);
    }
}
