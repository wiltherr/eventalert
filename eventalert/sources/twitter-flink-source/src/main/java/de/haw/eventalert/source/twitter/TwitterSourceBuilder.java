package de.haw.eventalert.source.twitter;

import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import de.haw.eventalert.source.twitter.util.PropertyUtil;
import org.apache.flink.streaming.connectors.twitter.TwitterSource;

import java.util.Properties;

public class TwitterSourceBuilder extends StatusesFilterEndpoint {

    TwitterSource twitterSource;

    public TwitterSourceBuilder() {
        this(PropertyUtil.getApiProperties());
    }

    public TwitterSourceBuilder(Properties twitterApiProperties) {
        twitterSource = new TwitterSource(twitterApiProperties);
    }

    public TwitterSource build() {
        twitterSource.setCustomEndpointInitializer(new EndpointInitializer());
        return twitterSource;
    }

    private class EndpointInitializer implements TwitterSource.EndpointInitializer {
        @Override
        public StreamingEndpoint createEndpoint() {
            return TwitterSourceBuilder.this;
        }
    }
}
