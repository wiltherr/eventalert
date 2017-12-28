package de.haw.eventalert.source.twitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * stream util class for events of {@link org.apache.flink.streaming.connectors.twitter.TwitterSource}
 */
public class TwitterSourceUtil {
    static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final String TWITTER_DATE_PATTERN = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
    static final SimpleDateFormat TWITTER_DATE_FORMAT = new SimpleDateFormat(TWITTER_DATE_PATTERN, Locale.ENGLISH);

    /**
     * converts the raw stream output of {@link org.apache.flink.streaming.connectors.twitter.TwitterSource} to {@link JsonNode} stream
     *
     * @param jsonStringTweetStream raw stream output of {@link org.apache.flink.streaming.connectors.twitter.TwitterSource}
     * @return stream of {@link JsonNode}s
     */
    public static DataStream<JsonNode> convertJson(DataStream<String> jsonStringTweetStream) {
        return jsonStringTweetStream.filter(StringUtils::isNotBlank).map(jsonTweet -> jsonMapper.readValue(jsonTweet, JsonNode.class));
    }

    /**
     * converts the raw stream output of {@link org.apache.flink.streaming.connectors.twitter.TwitterSource} to {@link Tweet} data object
     * @param jsonStringTweetStream raw stream output of {@link org.apache.flink.streaming.connectors.twitter.TwitterSource}
     * @return stream of {@link Tweet}s
     */
    public static DataStream<Tweet> convertToTweet(DataStream<String> jsonStringTweetStream) {
        return convertJson(jsonStringTweetStream).filter(Tweet::isValid).map(Tweet::fromJsonNode);
    }

    /**
     * count number of tweets in the given {@link WindowAssigner time window}
     *
     * @param tweetDataStream data stream of {@link Tweet}
     * @param windowAssigner  time window to count (you can only use windows of processing time)
     * @return number of tweets in the given windowAssigner
     */
    public static DataStream<Long> countTweets(DataStream<Tweet> tweetDataStream, WindowAssigner<Object, TimeWindow> windowAssigner) {
        return tweetDataStream.windowAll(windowAssigner).apply((AllWindowFunction<Tweet, Long, TimeWindow>) (window, tweets, out) -> {
            Long count = 0L;
            for (Tweet tweet : tweets)
                count++;
            out.collect(count);
        }).returns(Long.class).name("counting tweets in window");
    }
}
