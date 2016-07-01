package com.codepath.apps.mysimpletweets;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.LikeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.w3c.dom.Text;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class ProfileActivity extends AppCompatActivity {
    TwitterClient client;
    public ViewPager vpPager;
    public SmartFragmentStatePagerAdapter spPager;
    User user;
    String screenName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        //Get the screen name from activity that launches this
        screenName = getIntent().getStringExtra("screen_name");
        loadUserInfo(screenName);

        //Get the view pager
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        //creating smart pager adapter
        spPager = new TweetsPagerAdapter(getSupportFragmentManager());
        //Set the view pager adapter for the pager
        vpPager.setAdapter(spPager);
        //Find the pager sliding tabstrip
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //Attach the pager tabstrip to the view pager
        tabStrip.setViewPager(vpPager);

       /* if (savedInstanceState == null) {
            //Create the user timeline fragment
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(screenName);
            //Display the user timeline fragment within activity dynamically
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit();//changes the fragments
        }*/
    }

    public void loadUserInfo(String screenname){
        if (screenname != null && !screenname.isEmpty()){
            client.getUserInfo(screenname, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    user = User.fromJSON(response);
                    //populate header
                    populateProfileHeader(user);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });
        } else {
            client.getMyInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    user = User.fromJSON(response);
                    //populate header
                    populateProfileHeader(user);
                }
            });

        }
    }

    private void populateProfileHeader(User user) {
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        TextView tvScreenname = (TextView) findViewById(R.id.tvScreenName);
        ImageView ivProfileBanner = (ImageView) findViewById(R.id.ivProfileBanner);
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvScreenname.setText(user.getScreenName());
        tvName.setText(user.getName());
        tvTagline.setText(user.getTagline());
        tvFollowers.setText(String.valueOf(user.getFollowersCount()) + " Followers");
        tvFollowing.setText(String.valueOf(user.getFollowingsCount()) + " Following");
        if (user.getProfileBannerUrl() != null) {
            Picasso.with(this).load(user.getProfileBannerUrl()).into(ivProfileBanner);
        } else {
            ivProfileBanner.setBackgroundColor(Color.rgb(41, 156, 231));
        }
        Picasso.with(this).load(user.getProfileImageUrl()).transform(new RoundedCornersTransformation(3, 3)).into(ivProfileImage);
    }

    //Return the order of the fragments in the view pager
    public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {
        final int PAGE_COUNT = 2;
        private String tabTitles[] = {"Tweets", "Likes"};

        //how the adapter gets the manager to insert or remove items form activity
        public TweetsPagerAdapter(FragmentManager fm){
            super(fm);
        }

        //Controls order and creation of fragments within the apger
        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                return  UserTimelineFragment.newInstance(screenName);
            } else if (position == 1 ){
                return LikeTimelineFragment.newInstance(screenName);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
