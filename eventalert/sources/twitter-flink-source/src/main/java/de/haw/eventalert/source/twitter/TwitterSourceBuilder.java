package de.haw.eventalert.source.twitter;

import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.Location;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import org.apache.flink.shaded.com.google.common.collect.Lists;
import org.apache.flink.streaming.connectors.twitter.TwitterSource;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

public class TwitterSourceBuilder implements Serializable, TwitterSource.EndpointInitializer {

    private TwitterSource twitterSource;
    private List<String> languages;
    private List<Long> userIds;
    private List<String> terms;
    private List<Location> locations;

    public TwitterSourceBuilder() {
        this(ApiProperties.apiProperties);
    }

    public TwitterSourceBuilder(Properties twitterApiProperties) {
        twitterSource = new TwitterSource(twitterApiProperties);
        languages = Lists.newArrayList();
        userIds = Lists.newArrayList();
        terms = Lists.newArrayList();
        locations = Lists.newArrayList();
    }

    public TwitterSourceBuilder addLanguages(String... languages) {
        this.languages.addAll(Lists.newArrayList(languages));
        return this;
    }

    public TwitterSourceBuilder addFollowing(Long... userIds) {
        this.userIds.addAll(Lists.newArrayList(userIds));
        return this;
    }

    public TwitterSourceBuilder addTrackTerms(String... terms) {
        this.terms.addAll(Lists.newArrayList(terms));
        return this;
    }

    public TwitterSourceBuilder addLocations(List<Location> locations) {
        this.locations.addAll(Lists.newArrayList(locations));
        return this;
    }

    public TwitterSource build() {
        twitterSource.setCustomEndpointInitializer(this);
        return twitterSource;
    }

    @Override
    public StreamingEndpoint createEndpoint() {
        //see https://developer.twitter.com/en/docs/tweets/filter-realtime/guides/basic-stream-parameters
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        endpoint.languages(languages);
        endpoint.followings(userIds);
        endpoint.trackTerms(terms);
        endpoint.locations(locations);
        endpoint.delimited(false);
        endpoint.filterLevel(Constants.FilterLevel.None);
        return endpoint;
    }
}
