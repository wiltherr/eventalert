package de.haw.eventalert.core.producer.twitter;

import com.twitter.hbc.core.endpoint.Location;
import de.haw.eventalert.core.global.alertevent.AlertEvent;
import de.haw.eventalert.core.global.alertevent.AlertEvents;
import de.haw.eventalert.core.producer.AlertEventProducer;
import de.haw.eventalert.source.twitter.Tweet;
import de.haw.eventalert.source.twitter.TwitterSourceBuilder;
import de.haw.eventalert.source.twitter.TwitterSourceUtil;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.twitter.TwitterSource;

/**
 * example flink job with {@link TwitterSourceBuilder} and {@link TwitterSourceUtil}
 * <p>
 * the job get all german tweets with keywords hamburg or #hamburg
 * and tweets that are tweeted in geolocation hamburg.
 * the tweets will be converted to alertEvents and emitted to EventAlert
 */
public class TwitterAlertEventProducerExample {

    private static final Location HAMBURG = new Location(new Location.Coordinate(9.75, 53.35), new Location.Coordinate(10.24, 53.68));

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
        //create a new twitter source builder
        TwitterSourceBuilder builder = new TwitterSourceBuilder();

        //only get tweets in german language
        builder.addLanguageFilter("de");
        //get tweets with keyword hamburg or #hamburg
        builder.addKeyWordFilter("hamburg", "#hamburg");
        //get tweets in geolocation of Hamburg
        builder.addLocationFilter(HAMBURG);
        //build the twitter source
        TwitterSource twitterSource = builder.build();

        //add source to environment
        DataStream<String> dataStream = environment.addSource(twitterSource);

        //convert raw strings to tweet data object and filter tweets that are not a retweet
        DataStream<Tweet> tweetStream = TwitterSourceUtil.convertToTweet(dataStream).filter(tweet -> !tweet.isRetweet());
        //create a new alertEvent out of tweet
        DataStream<AlertEvent> alertEventStream = tweetStream.map(tweet -> AlertEvents.createEvent("TweetEvent", tweet));

        //provide alertEventStream to EventAlert
        AlertEventProducer.createAlertEventProducer(alertEventStream);

        //execute job
        environment.execute();
    }
}
