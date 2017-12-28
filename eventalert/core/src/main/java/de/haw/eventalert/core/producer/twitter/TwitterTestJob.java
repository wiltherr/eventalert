package de.haw.eventalert.core.producer.twitter;

import com.twitter.hbc.core.endpoint.Location;
import de.haw.eventalert.source.twitter.Tweet;
import de.haw.eventalert.source.twitter.TwitterSourceBuilder;
import de.haw.eventalert.source.twitter.TwitterSourceUtil;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class TwitterTestJob {
    public static final Location HAMBURG = new Location(new Location.Coordinate(9.75, 53.35), new Location.Coordinate(10.24, 53.68));

    public static void main(String[] args) throws Exception {

        final StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        TwitterSourceBuilder builder = new TwitterSourceBuilder();
        builder.addLanguageFilter("de");
        builder.addKeyWordFilter("hamburg");
        builder.addLocationFilter(HAMBURG);
        DataStream<String> dataStream = environment.addSource(builder.build());
        DataStream<Tweet> tweetStream = TwitterSourceUtil.convertToTweet(dataStream);
        //TwitterSourceUtil.countTweets(tweetStream, TumblingProcessingTimeWindows.of(Time.seconds(5))).print();
        tweetStream.filter(tweet -> !tweet.isRetweet()).map(Tweet::getContent).print();
        environment.execute();
    }
}
