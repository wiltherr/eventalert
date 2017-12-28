package de.haw.eventalert.core.producer.twitter;

import com.fasterxml.jackson.databind.JsonNode;
import de.haw.eventalert.core.global.util.Utils;
import de.haw.eventalert.source.twitter.Tweet;
import de.haw.eventalert.source.twitter.TwitterSourceBuilder;
import de.haw.eventalert.source.twitter.TwitterSourceUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class TwitterTestJob {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        TwitterSourceBuilder builder = new TwitterSourceBuilder();
        builder.addLanguages("de");
        builder.addTrackTerms("xmas", "weihnachten");
        DataStream<String> dataStream = environment.addSource(builder.build());
        DataStream<Tweet> tweetDataStream = TwitterSourceUtil.convertToTweet(dataStream);

        dataStream.filter(StringUtils::isNotBlank).print();
        dataStream.flatMap(((value, out) -> {
            JsonNode jsonNode = Utils.jsonMapper.readValue(value, JsonNode.class);
            if (jsonNode.has("text")) {
                String tweet = jsonNode.get("text").asText();
                if (StringUtils.isNotBlank(tweet))
                    out.collect(tweet);
            }
        })).print();
        environment.execute();
    }
}
