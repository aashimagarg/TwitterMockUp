package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static com.squareup.picasso.Picasso.*;

/**
 * Created by aashimagarg on 6/28/16.
 */
//Taking the tweet objects and turning them into Views that will be displayed in lists
public class TweetsArrayAdapter extends ArrayAdapter<Tweet>{

    TwitterClient client = TwitterApplication.getRestClient();


    public TweetsArrayAdapter(Context context, List<Tweet> tweets){
        super(context, android.R.layout.simple_list_item_1, tweets);
    }

    //Override and setup custom template
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //1. Get the tweet
        final Tweet tweet = getItem(position);
        //2. Find/inflate the template
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
        }
        //3. Find the subviews to fill with data in the template
        final ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.ivImage);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvFullName);
        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tvTimeStamp);
        Button btnReply = (Button) convertView.findViewById(R.id.btnReply);
        ImageView ivMedia = (ImageView) convertView.findViewById(R.id.ivMedia);
        final Button btnRetweet = (Button) convertView.findViewById(R.id.btnRetweet);
        final Button btnLike = (Button) convertView.findViewById(R.id.btnLike);

        //4. populate data into subviews
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),"fonts/GothamNBold.otf");
        Typeface tf2 = Typeface.createFromAsset(getContext().getAssets(),"fonts/GothamNBook.otf");
        tvTime.setText(tweet.getCreatedAt());
        tvTime.setTypeface(tf2);
        tvUserName.setText(tweet.getUser().getScreenName());
        tvUserName.setTypeface(tf2);
        tvName.setText(tweet.getUser().getName());
        tvName.setTypeface(tf2);
        tvBody.setText(tweet.getBody());
        tvBody.setTypeface(tf2);

        ivProfileImage.setImageResource(android.R.color.transparent);
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).transform(new RoundedCornersTransformation(3, 3)).into(ivProfileImage);

        //showing images of tweets - very hacky way.
        if (tweet.getMedia() != null){
            ivMedia.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(tweet.getMedia()).transform(new RoundedCornersTransformation(3, 3)).into(ivMedia);
        } else {
            ivMedia.setVisibility(View.GONE);
        }

        if (tweet.isRetweeted()){
            btnRetweet.setBackgroundResource(R.drawable.retweet_action_on);
        } else {
            btnRetweet.setBackgroundResource(R.drawable.retweet_action);
        }
        if (tweet.isFavorited()){
            btnLike.setBackgroundResource(R.drawable.like_action_on);
        } else {
            btnLike.setBackgroundResource(R.drawable.like_action);
        }

        //set on image click listener
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ivProfileImage != null) {
                    // Fire an intent when a photo is selected
                    Intent i = new Intent(v.getContext(), ProfileActivity.class);
                    // Pass contact object in the bundle and populate details activity.
                    i.putExtra("screen_name", tweet.getUser().getScreenName());
                    v.getContext().startActivity(i);
                }
            }
        });

        //set reply click listener
        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Fire an intent when a photo is selected
                Intent i = new Intent(v.getContext(), ComposeActivity.class);
                // Pass contact object in the bundle and populate details activity.
                i.putExtra("screen_name", tweet.getUser().getScreenName());
                v.getContext().startActivity(i);

            }
        });


        //set retweet click listener
        btnRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {

                    if (!tweet.isRetweeted()) {
                        client.postRetweet(tweet.getUid(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                btnRetweet.setBackgroundResource(R.drawable.retweet_action_on);
                                tweet.isRetweeted = true;
                                //try to figure out how to make tweet appear on profile view without a refresh (in real time)
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            }

                        });
                    } else {
                        client.postUnRetweet(tweet.getUid(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                btnRetweet.setBackgroundResource(R.drawable.retweet_action);
                                tweet.isRetweeted = false;
                                //try to figure out how to make tweet appear on profile view without a refresh (in real time)
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            }

                        });
                    }
                }

        });

        //set favorite click listener
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {

                if (!tweet.isFavorited()) {
                    client.postFavorite(tweet.getUid(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            btnLike.setBackgroundResource(R.drawable.like_action_on);
                            tweet.isFavorited = true;
                            //try to figure out how to make tweet appear on profile view without a refresh (in real time)
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        }

                    });
                } else {
                    client.postUnFavorite(tweet.getUid(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            btnLike.setBackgroundResource(R.drawable.like_action);
                            tweet.isFavorited = false;
                            //try to figure out how to make tweet appear on profile view without a refresh (in real time)
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        }

                    });
                }
            }

        });


        //5. return the view to be inserted into the list
        return convertView;
    }
}
