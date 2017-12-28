package de.haw.eventalert.source.twitter;

import com.fasterxml.jackson.databind.JsonNode;

import java.text.ParseException;

/**
 * data object for twitter tweets
 */
public class Tweet {

    private final static String JSON_FIELD_ID = "id";
    private final static String JSON_FIELD_TEXT = "text";
    private final static String JSON_FIELD_CREATED = "created_at";
    private final static String JSON_FIELD_USER = "user";
    private final static String JSON_FIELD_USER_ID = "id";
    private final static String JSON_FIELD_USER_SCREEN_NAME = "screen_name";
    private final static String JSON_FIELD_USER_USER_NAME = "name";
    private final static String JSON_FIELD_RETWEET_STATUS = "retweeted_status";
    private final static String JSON_FIELD_EXTENDED = "extended_tweet";
    private final static String JSON_FIELD_FULL_TEXT = "full_text";

    private long id;
    private String content;
    private long createdTimestamp;
    private long userId;
    private String userScreenName;
    private String userName;
    private boolean retweet;
    private String retweetContent;

    private Tweet() {
    }

    /**
     * check if the given jsonNode is valid for converting
     *
     * @param jsonTweet twitter tweet as json node (raw strings can be converted with {@link TwitterSourceUtil})
     * @return true if jsonNode is valid and can be converted
     */
    public static boolean isValid(JsonNode jsonTweet) {
        return jsonTweet.hasNonNull(JSON_FIELD_ID)
                && jsonTweet.hasNonNull(JSON_FIELD_TEXT)
                && jsonTweet.hasNonNull(JSON_FIELD_CREATED)
                && jsonTweet.hasNonNull(JSON_FIELD_USER);
    }

    /**
     * converts a json tweet to {@link Tweet} data object.
     * note: the converted {@link Tweet} object has not all fields of the parent {@link JsonNode}!
     * check if the tweet is valid (with {@link #isValid(JsonNode)}) before converting.
     *
     * @param jsonTweet twitter tweet as json node (raw strings can be converted with {@link TwitterSourceUtil})
     * @return a new {@link Tweet} data object
     */
    public static Tweet fromJsonNode(JsonNode jsonTweet) {
        //see https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/tweet-object
        Tweet tweet = new Tweet();
        //Tweet
        tweet.setId(jsonTweet.get(JSON_FIELD_ID).asLong(0));
        if (jsonTweet.hasNonNull(JSON_FIELD_EXTENDED)) {
            tweet.setContent(jsonTweet.get(JSON_FIELD_EXTENDED).get(JSON_FIELD_FULL_TEXT).asText(""));
        } else {
            tweet.setContent(jsonTweet.get(JSON_FIELD_TEXT).asText(""));
        }
        try {
            tweet.setCreatedTimestamp(TwitterSourceUtil.TWITTER_DATE_FORMAT.parse(jsonTweet.get(JSON_FIELD_CREATED).asText("")).getTime() / 1000);
        } catch (ParseException e) {
            tweet.setCreatedTimestamp(0);
        }
        //User
        JsonNode jsonUser = jsonTweet.get(JSON_FIELD_USER);
        tweet.setUserId(jsonUser.get(JSON_FIELD_USER_ID).asLong(0));
        tweet.setUserScreenName(jsonUser.get(JSON_FIELD_USER_SCREEN_NAME).asText(""));
        tweet.setUserName(jsonUser.get(JSON_FIELD_USER_USER_NAME).asText(""));
        //retweet
        if (jsonTweet.has(JSON_FIELD_RETWEET_STATUS)) {
            tweet.setRetweet(true);
            JsonNode retweetJson = jsonTweet.get(JSON_FIELD_RETWEET_STATUS);
            if (retweetJson.hasNonNull(JSON_FIELD_EXTENDED))
                tweet.setRetweetContent(retweetJson.get(JSON_FIELD_EXTENDED).get(JSON_FIELD_FULL_TEXT).asText(""));
            else
                tweet.setRetweetContent(retweetJson.get(JSON_FIELD_TEXT).asText(""));
        } else {
            tweet.setRetweet(false);
            tweet.setRetweetContent(null);
        }
        return tweet;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTime) {
        this.createdTimestamp = createdTime;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserScreenName() {
        return userScreenName;
    }

    public void setUserScreenName(String userScreenName) {
        this.userScreenName = userScreenName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isRetweet() {
        return retweet;
    }

    public void setRetweet(boolean retweet) {
        this.retweet = retweet;
    }

    public String getRetweetContent() {
        return retweetContent;
    }

    public void setRetweetContent(String retweetContent) {
        this.retweetContent = retweetContent;
    }
}
