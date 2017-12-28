package de.haw.eventalert.source.twitter;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TweetTest {

    @Test
    void isValid() throws IOException {
        assertTrue(Tweet.isValid(loadTweetJson("tweets/with_retweet_extended_text.json")));
        assertTrue(Tweet.isValid(loadTweetJson("tweets/with_extended_text.json")));
        assertTrue(Tweet.isValid(loadTweetJson("tweets/with_retweet.json")));
        assertFalse(Tweet.isValid(loadTweetJson("tweets/invalid.json")));
        assertFalse(Tweet.isValid(loadTweetJson("tweets/invalid_without_text.json")));
        assertFalse(Tweet.isValid(loadTweetJson("tweets/invalid_null_user.json")));
    }

    @Test
    void testFromJsonNode1() throws IOException {
        Tweet actualTestTweet = Tweet.fromJsonNode(loadTweetJson("tweets/with_retweet_extended_text.json"));
        final long id = 946170081477910533L;
        final String content = "RT @tagesschau: - Ein sechsjähriges Mädchen\n" + "- Ein geschenktes Handy zu Weihnachten\n" + "- 19 Notrufe bei der Polizei - Hier die entsprechende Na…";
        final long time = 1514419501L;
        final long userId = 20941499L;
        final String userName = "Martin";
        final String userScreenName = "macwinnie";
        final boolean retweetStatus = true;
        final String retweetContent = "- Ein sechsjähriges Mädchen\n- Ein geschenktes Handy zu Weihnachten\n- 19 Notrufe bei der Polizei - Hier die entsprechende Nachricht zu Weihnachten: \nhttps://t.co/6BAdrtyC7F";

        assertEquals(id, actualTestTweet.getId());
        assertEquals(content, actualTestTweet.getContent());
        assertEquals(time, actualTestTweet.getCreatedTimestamp());
        assertEquals(userId, actualTestTweet.getUserId());
        assertEquals(userName, actualTestTweet.getUserName());
        assertEquals(userScreenName, actualTestTweet.getUserScreenName());
        assertEquals(retweetStatus, actualTestTweet.isRetweet());
        assertEquals(retweetContent, actualTestTweet.getRetweetContent());
    }

    @Test
    void testFromJsonNode2() throws IOException {
        Tweet actualTestTweet = Tweet.fromJsonNode(loadTweetJson("tweets/with_extended_text.json"));
        final long id = 946340632326492160L;
        final String content = "Xmas Lucky Draw Ich amüsiere mich beim WeGamers-Weihnachtsverlosungsevent! Sei auch dabei und hol dir Spielboni und Geschenkgutscheine! https://t.co/LpF3jJSEv9";
        final long time = 1514460163L;
        final long userId = 774936007460450304L;
        final String userName = "benny_w176";
        final String userScreenName = "benny_w176";
        final boolean retweetStatus = false;
        final String retweetContent = null;

        assertEquals(id, actualTestTweet.getId());
        assertEquals(content, actualTestTweet.getContent());
        assertEquals(time, actualTestTweet.getCreatedTimestamp());
        assertEquals(userId, actualTestTweet.getUserId());
        assertEquals(userName, actualTestTweet.getUserName());
        assertEquals(userScreenName, actualTestTweet.getUserScreenName());
        assertEquals(retweetStatus, actualTestTweet.isRetweet());
        assertEquals(retweetContent, actualTestTweet.getRetweetContent());
    }

    @Test
    void testFromJsonNode3() throws IOException {
        Tweet actualTestTweet = Tweet.fromJsonNode(loadTweetJson("tweets/with_retweet.json"));
        final long id = 946348291771588608L;
        final String content = "RT @DoitCosplaygirl: Using YOUR comments on my Xmas video to play a game: Which country is that tradition from? Let's play! https://t.co/OV…";
        final long time = 1514461989L;
        final long userId = 3206899871L;
        final String userName = "Katy Black";
        final String userScreenName = "katyblackieblak";
        final boolean retweetStatus = true;
        final String retweetContent = "Using YOUR comments on my Xmas video to play a game: Which country is that tradition from? Let's play! https://t.co/OVzNGUD4wP";

        assertEquals(id, actualTestTweet.getId());
        assertEquals(content, actualTestTweet.getContent());
        assertEquals(time, actualTestTweet.getCreatedTimestamp());
        assertEquals(userId, actualTestTweet.getUserId());
        assertEquals(userName, actualTestTweet.getUserName());
        assertEquals(userScreenName, actualTestTweet.getUserScreenName());
        assertEquals(retweetStatus, actualTestTweet.isRetweet());
        assertEquals(retweetContent, actualTestTweet.getRetweetContent());
    }

    private JsonNode loadTweetJson(String fileName) throws IOException {
        ClassLoader classLoader = TweetTest.class.getClassLoader();
        return TwitterSourceUtil.jsonMapper.readValue(classLoader.getResource(fileName), JsonNode.class);
    }

}