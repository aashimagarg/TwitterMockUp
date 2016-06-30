package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aashimagarg on 6/28/16.
 */
public class TweetsListFragment extends Fragment {
    protected ArrayList<Tweet> tweets;
    protected TweetsArrayAdapter aTweets;
    protected ListView lvTweets;


    //1. inflation logic
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        //Find the list view
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        //Connect adapter to list view
        lvTweets.setAdapter(aTweets);
        return v;
    }

    public void addAll(List<Tweet> tweets){
        aTweets.addAll(tweets);
    }

    //2. creation lifecycle event
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Create the arraylist (data source)
        tweets = new ArrayList<>();
        //Create the adapater from data source
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);
    }
}
