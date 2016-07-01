package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by aashimagarg on 6/30/16.
 */
public class LikeTimelineFragment extends TweetsListFragment {

    private TwitterClient client;


    // Creates a new fragment given an int and title
    // DemoFragment.newInstance(5, "Hello");
    public static LikeTimelineFragment newInstance(String screen_name) {
        LikeTimelineFragment likeFragment = new LikeTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screen_name);
        likeFragment.setArguments(args);
        return likeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateTimeline();
    }

    //Send an API request to get Timeline Json
    //fill the listview by creating the tweet objects form the Json
    private void populateTimeline() {
        client.getLikesTimeline((getArguments().getString("screen_name")), new JsonHttpResponseHandler() {
            //SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                super.onSuccess(statusCode, headers, json);
                Log.d("DEBUG", json.toString());
                //DESERIALIZE JSON
                //CREATE MODELS + ADD TO ADAPTER
                //LOAD THE MODEL DATA INTO LV
                addAll(Tweet.fromJSONArray(json));
            }

            //FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("DEBUG", errorResponse.toString());
            }

        });
    }
}
