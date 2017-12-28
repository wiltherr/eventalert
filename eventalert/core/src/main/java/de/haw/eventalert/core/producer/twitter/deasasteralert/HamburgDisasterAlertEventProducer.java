package de.haw.eventalert.core.producer.twitter.deasasteralert;

import com.twitter.hbc.core.endpoint.Location;
import de.haw.eventalert.core.global.AlertEvents;
import de.haw.eventalert.core.global.entity.event.AlertEvent;
import de.haw.eventalert.core.producer.AlertEventProducer;
import de.haw.eventalert.source.twitter.Tweet;
import de.haw.eventalert.source.twitter.TwitterSourceBuilder;
import de.haw.eventalert.source.twitter.TwitterSourceUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.shaded.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.twitter.TwitterSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * example flink job with {@link TwitterSourceBuilder} and {@link TwitterSourceUtil}
 * <p>
 * the job get all german tweets with keywords hamburg or #hamburg and tweets that are tweeted in geolocation Hamburg.
 * the tweets will be converted to alertEvents and emitted to EventAlert
 */
public class HamburgDisasterAlertEventProducer {
    private static final Logger LOG = LoggerFactory.getLogger(HamburgDisasterAlertEventProducer.class);

    private static final Location HAMBURG = new Location(new Location.Coordinate(9.75, 53.35), new Location.Coordinate(10.24, 53.68));

    private static final Long disasterTweetThreshold = 100L;
    private static final List<String> disasterKeyWords = Lists.newArrayList("Gefahr", "Katastrophe", "Terror", "Evakuierung", "Anschlag", "Attentat", "Unfall");

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);

        TwitterSource twitterSource = new TwitterSourceBuilder()
                .addLanguageFilter("de")
                .addKeyWordFilter("hamburg", "#hamburg", "hh", "#hh")
                .addLocationFilter(HAMBURG)
                .build();
        DataStream<Tweet> tweetDataStream = TwitterSourceUtil.convertToTweet(environment.addSource(twitterSource));
        DataStream<Tweet> disasterTweets = tweetDataStream.flatMap(((tweet, out) -> {
            if (disasterKeyWords.stream().anyMatch(keyword -> StringUtils.containsIgnoreCase(tweet.getContent(), keyword))) {
                LOG.info("disaster tweet was detected: {}", tweet.getContent());
                out.collect(tweet);
            } else {
                LOG.debug("Tweet was no disaster tweet: {}", tweet.getContent());
            }
        }));
        DataStream<DisasterAlert> disasterAlertStream = TwitterSourceUtil.countTweets(disasterTweets,
                //check ever minute if the threshold is exceeded. look at all disaster tweets of the last hour
                SlidingProcessingTimeWindows.of(Time.hours(1), Time.minutes(1)))
                .flatMap((count, out) -> {
                    LOG.info("count disaster tweets: {} disaster tweets in the last hour.", count);
                    if (count >= disasterTweetThreshold) {
                        LOG.info("!!threshold exceeded! disasterAlert will be emitted!!");
                        out.collect(new DisasterAlert("Hamburg"));
                    }
                });

        DataStream<AlertEvent> alertEventStream = disasterAlertStream.map(disasterAlert -> AlertEvents.createEvent(DisasterAlert.EVENT_TYPE, disasterAlert));
        AlertEventProducer.createAlertEventProducer(alertEventStream);
        environment.execute();
    }
}
