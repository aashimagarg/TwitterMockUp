package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.TweetsListFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    final int COMPOSE_REQUEST_CODE = 55;
    public SmartFragmentStatePagerAdapter spPager;
    User user;
    TwitterClient client;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tabs) PagerSlidingTabStrip tabStrip;
    @BindView(R.id.viewpager) ViewPager vpPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        client = TwitterApplication.getRestClient();
        client.getMyInfo(new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    user = User.fromJSON(response);
                    Log.d("DEBUG", "USER WAS NULL");
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG","went to on failure");
                    Log.d("DEBUG",errorResponse.toString());
                }
            });

        // Display icon in the toolbar
        /*getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_twitter);
        getSupportActionBar().setDisplayUseLogoEnabled(true);*/

        //Get the view pager
        //vpPager = (ViewPager) findViewById(R.id.viewpager);
        //creating smart pager adapter
        spPager = new TweetsPagerAdapter(getSupportFragmentManager());
        //Set the view pager adapter for the pager
        vpPager.setAdapter(spPager);
        //Find the pager sliding tabstrip
        //PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //Attach the pager tabstrip to the view pager
        tabStrip.setViewPager(vpPager);
    }

    //Return the order of the fragments in the view pager
    public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {
        final int PAGE_COUNT = 2;
        private String tabTitles[] = {"Home", "Mentions"};

        //how the adapter gets the manager to insert or remove items form activity
        public TweetsPagerAdapter(FragmentManager fm){
            super(fm);
        }

        //Controls order and creation of fragments within the apger
        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                return  new HomeTimelineFragment();
            } else if (position == 1 ){
                return new MentionsTimelineFragment();
            } else {
                return null;
            }
        }

        //Return the tab title
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        //How many fragments there are to swipe between
        @Override
        public int getCount() {
            return tabTitles.length;
        }

    }
    public void onProfileView(MenuItem mi){
        //Launch the profile view
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("screen_name", user.getScreenName());
        startActivity(i);
    }

    public void onNewTweet(MenuItem mi){
        //Launch the compose view
        Intent i = new Intent(this, ComposeActivity.class);
        //i.putExtra("screen_name", user.getScreenName());
        startActivityForResult(i, COMPOSE_REQUEST_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //return from compose
        if (requestCode == COMPOSE_REQUEST_CODE && resultCode == RESULT_OK){
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            // Inside `onActivityResult` in the main `TweetsActivity`
            // Pass new tweet into the home timeline and add to top of the list
            HomeTimelineFragment fragmentHomeTweets =
                    (HomeTimelineFragment) spPager.getRegisteredFragment(0);
            fragmentHomeTweets.appendTweet(tweet);

        }
        if (requestCode == COMPOSE_REQUEST_CODE && resultCode == RESULT_CANCELED){

        }

    }
}
