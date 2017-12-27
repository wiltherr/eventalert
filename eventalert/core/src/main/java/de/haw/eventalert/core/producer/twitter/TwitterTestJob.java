package de.haw.eventalert.core.producer.twitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import de.haw.eventalert.core.global.util.Utils;
import de.haw.eventalert.source.twitter.TwitterSourceBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class TwitterTestJob {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        TwitterSourceBuilder builder = new TwitterSourceBuilder();
        builder.languages(Lists.newArrayList("en", "de"));
        builder.trackTerms(Lists.newArrayList("xmas"));

        environment.addSource(builder.build()).filter(StringUtils::isNotBlank).flatMap(((value, out) -> {
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
