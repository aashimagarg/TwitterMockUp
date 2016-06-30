package com.codepath.apps.mysimpletweets.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by aashimagarg on 6/28/16.
 */

// 1. Parse the JSON + Store the data
// 2. Encapsulate state logic or display logic
@Parcel
public class Tweet {
    //list out attributes
    public String body;
    public long uid; //unique id for the tweet
    public User user;
    public String createdAt;

    public Tweet(){
    }

    //Deserialize the JSON and build Tweet objects
    //Tweet.fromJSON("{...}") => <Tweet>
    public static Tweet fromJSON(JSONObject jsonObject){
        Tweet tweet = new Tweet();
        //Extract values form the JSON
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Return the tweet object
        return tweet;
    }

    //Tweet.fromJSONArray({...}, {...}) => List<Tweet>
    public static ArrayList<Tweet> fromJSONArray(JSONArray array){
        ArrayList<Tweet> tweets = new ArrayList<>();
        //Iterate through json array and create tweets
        for (int i = 0; i < array.length(); i++){
            try {
                JSONObject tweetJson = array.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);
                if (tweet != null){
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                //even if one tweet doesnt work, continue other tweets
                continue;
            }

        }
        return tweets;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return format(getRelativeTimeAgo(createdAt));
    }

    public User getUser() {
        return user;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterTime;
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public String format(String reldate){
        int space = reldate.indexOf(" ");
        if (reldate.contains("second")) {
            reldate = reldate.substring(0, space);
            reldate += "s";
        }
        else if (reldate.contains("minute")) {
            reldate = reldate.substring(0, space);
            reldate += "m";
        }
        else if (reldate.contains("hour")) {
            reldate = reldate.substring(0, space);
            reldate += "h";
        }

        return reldate;
    }

}
