package de.haw.eventalert.source.twitter;

import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.Location;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.shaded.com.google.common.collect.Lists;
import org.apache.flink.streaming.connectors.twitter.TwitterSource;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * custom twitter source builder
 * builds a apache flink {@link TwitterSource} with custom {@link org.apache.flink.streaming.connectors.twitter.TwitterSource.EndpointInitializer EndpointInitializer}
 * <p>
 * see at <a href="https://developer.twitter.com/en/docs/tweets/filter-realtime/guides/basic-stream-parameter">https://developer.twitter.com/en/docs/tweets/filter-realtime/guides/basic-stream-parameter</a>
 * how a {@link StatusesFilterEndpoint} works.
 */
public class TwitterSourceBuilder implements Serializable, TwitterSource.EndpointInitializer {

    final static Function<Location, Tuple4<Double, Double, Double, Double>> convertLocationToTuple4 = location -> Tuple4.of(
            location.southwestCoordinate().longitude(),
            location.southwestCoordinate().latitude(),
            location.northeastCoordinate().longitude(),
            location.northeastCoordinate().latitude());
    final static Function<Tuple4<Double, Double, Double, Double>, Location> convertTuple4ToLocation = locationTuple ->
            new Location(new Location.Coordinate(locationTuple.f0, locationTuple.f1),
                    new Location.Coordinate(locationTuple.f2, locationTuple.f3));
    //TODO check maximum number of filters for Twitter-Track-API (see https://developer.twitter.com/en/docs/tweets/filter-realtime/overview)
    private TwitterSource twitterSource;
    private List<String> languages;
    private List<Long> userIds;
    private List<String> terms;
    private List<Tuple4<Double, Double, Double, Double>> locationTuples;

    /**
     * creates a builder with default twitter api configuration (defined in property-file)
     * <p>note: at least one keyword has to be added with {@link #addKeyWordFilter(String...)} to builder.
     *
     * @see #TwitterSourceBuilder(Properties) for more information
     */
    public TwitterSourceBuilder() {
        this(ApiProperties.apiProperties);
    }

    /**
     * creates a builder with given twitter api configuration
     * <p>note: at least one keyword has to be added with {@link #addKeyWordFilter(String...)} to builder.
     *
     * @param twitterApiProperties twitter api configuration
     * @see <a href="https://ci.apache.org/projects/flink/flink-docs-release-1.3/dev/connectors/twitter.html">https://ci.apache.org/projects/flink/flink-docs-release-1.3/dev/connectors/twitter.html</a>
     * @see TwitterSource
     */
    public TwitterSourceBuilder(Properties twitterApiProperties) {
        twitterSource = new TwitterSource(twitterApiProperties);
        languages = Lists.newArrayList();
        userIds = Lists.newArrayList();
        terms = Lists.newArrayList();
        locationTuples = Lists.newArrayList();
    }

    /**
     * filter tweets by language codes
     *
     * @param languages language code(s) (f.e. "en" or "de")
     * @return builder object
     * @see <a href="https://developer.twitter.com/en/docs/tweets/filter-realtime/guides/basic-stream-parameters">https://developer.twitter.com/en/docs/tweets/filter-realtime/guides/basic-stream-parameters</a>
     */
    public TwitterSourceBuilder addLanguageFilter(String... languages) {
        this.languages.addAll(Lists.newArrayList(languages));
        return this;
    }

    /**
     * filter tweets by users
     *
     * @param userIds id(s) of user
     * @return builder object
     * @see <a href="https://developer.twitter.com/en/docs/tweets/filter-realtime/guides/basic-stream-parameters">https://developer.twitter.com/en/docs/tweets/filter-realtime/guides/basic-stream-parameters</a>
     */
    public TwitterSourceBuilder addUserFilter(Long... userIds) {
        this.userIds.addAll(Lists.newArrayList(userIds));
        return this;
    }

    /**
     * filter by keywords
     * <p>
     * note that hashtags (#) are not added automatically
     * you have to add hashtag keywords manual. f.e.
     * add "#Hamburg" and "Hamburg", if you want to get tweets with both keywords.
     *
     * @param terms keyword(s) to filter
     * @return builder object
     * @see <a href="https://developer.twitter.com/en/docs/tweets/filter-realtime/guides/basic-stream-parameters">https://developer.twitter.com/en/docs/tweets/filter-realtime/guides/basic-stream-parameters</a>
     */
    public TwitterSourceBuilder addKeyWordFilter(String... terms) {
        this.terms.addAll(Lists.newArrayList(terms));
        return this;
    }

    /**
     * filter by a geographic location
     *
     * @param locations coordinates of location(s)
     * @return builder object
     * @see <a href="https://developer.twitter.com/en/docs/tweets/filter-realtime/guides/basic-stream-parameters">https://developer.twitter.com/en/docs/tweets/filter-realtime/guides/basic-stream-parameters</a>
     */
    public TwitterSourceBuilder addLocationFilter(Location... locations) {
        this.locationTuples.addAll(
                //convert twitter location to tuple (for serialization)
                Stream.of(locations).map(convertLocationToTuple4).collect(Collectors.toList())
        );
        return this;
    }

    /**
     * build the source with specified configuration
     *
     * @return a new TwitterSource
     * @throws IllegalStateException if no keywords were added to builder. at least one keyword have to be defined!
     * @see <a href="https://developer.twitter.com/en/docs/tweets/filter-realtime/guides/basic-stream-parameters">https://developer.twitter.com/en/docs/tweets/filter-realtime/guides/basic-stream-parameters</a>
     */
    public TwitterSource build() {
        if (terms.isEmpty()) {
            throw new IllegalStateException("at least one keywords has to be set!");
        }
        twitterSource.setCustomEndpointInitializer(this);
        return twitterSource;
    }

    @Override
    public StreamingEndpoint createEndpoint() {
        if (locationTuples.isEmpty() && userIds.isEmpty() && terms.isEmpty()) {
            throw new IllegalStateException("at least on filtering by following, terms or locationTuples has to be set!");
        }
        //see https://developer.twitter.com/en/docs/tweets/filter-realtime/guides/basic-stream-parameters for endpoint fields
        StatusesFilterEndpoint resultEndpoint = new StatusesFilterEndpoint();
        if (!userIds.isEmpty())
            resultEndpoint.followings(userIds);
        if (!terms.isEmpty())
            resultEndpoint.trackTerms(terms);
        if (!locationTuples.isEmpty()) {
            resultEndpoint.locations(
                    //convert tuples back to location
                    locationTuples.stream().map(convertTuple4ToLocation).collect(Collectors.toList())
            );
        }

        if (!languages.isEmpty())
            resultEndpoint.languages(languages);
        resultEndpoint.delimited(false);
        resultEndpoint.filterLevel(Constants.FilterLevel.None);
        return resultEndpoint;
    }
}
