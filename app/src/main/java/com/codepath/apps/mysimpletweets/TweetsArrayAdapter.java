package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.widget.RecyclerView;
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

import butterknife.BindView;
import butterknife.ButterKnife;
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

    static class ViewHolder{
        @BindView(R.id.ivImage) ImageView ivProfileImage;
        @BindView(R.id.tvUserName) TextView tvUserName;
        @BindView(R.id.tvFullName) TextView tvName;
        @BindView(R.id.tvBody) TextView tvBody;
        @BindView(R.id.tvTimeStamp) TextView tvTime;
        @BindView(R.id.tvRTCount) TextView tvRTCount;
        @BindView(R.id.tvLikeCount) TextView tvLikeCount;
        @BindView(R.id.btnReply) Button btnReply;
        @BindView(R.id.ivMedia) ImageView ivMedia;
        @BindView(R.id.btnRetweet) Button btnRetweet;
        @BindView(R.id.btnLike) Button btnLike;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    //Override and setup custom template
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        //1. Get the tweet
        final Tweet tweet = getItem(position);
        //2. Find/inflate the template

        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }



        //3. Find the subviews to fill with data in the template

        //4. populate data into subviews
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),"fonts/GothamNBold.otf");
        Typeface tf2 = Typeface.createFromAsset(getContext().getAssets(),"fonts/GothamNBook.otf");
        viewHolder.tvTime.setText(tweet.getCreatedAt());
        viewHolder.tvTime.setTypeface(tf2);
        viewHolder.tvUserName.setText(tweet.getUser().getScreenName());
        viewHolder.tvUserName.setTypeface(tf2);
        viewHolder.tvName.setText(tweet.getUser().getName());
        viewHolder.tvName.setTypeface(tf2);
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvBody.setTypeface(tf2);
        viewHolder.tvRTCount.setText(String.valueOf(tweet.getRtCount()));
        viewHolder.tvLikeCount.setText(String.valueOf(tweet.getLikeCount()));

        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).transform(new RoundedCornersTransformation(3, 3)).into(viewHolder.ivProfileImage);

        //showing images of tweets - very hacky way.
        if (tweet.getMedia() != null){
            viewHolder.ivMedia.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(tweet.getMedia()).transform(new RoundedCornersTransformation(3, 3)).into(viewHolder.ivMedia);
        } else {
            viewHolder.ivMedia.setVisibility(View.GONE);
        }

        if (tweet.isRetweeted()){
            viewHolder.btnRetweet.setBackgroundResource(R.drawable.retweet_action_on);
        } else {
            viewHolder.btnRetweet.setBackgroundResource(R.drawable.retweet_action);
        }
        if (tweet.isFavorited()){
            viewHolder.btnLike.setBackgroundResource(R.drawable.like_action_on);
        } else {
            viewHolder.btnLike.setBackgroundResource(R.drawable.like_action);
        }

        //set on image click listener
        viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (viewHolder.ivProfileImage != null) {
                    // Fire an intent when a photo is selected
                    Intent i = new Intent(v.getContext(), ProfileActivity.class);
                    // Pass contact object in the bundle and populate details activity.
                    i.putExtra("screen_name", tweet.getUser().getScreenName());
                    v.getContext().startActivity(i);
                }
            }
        });

        //set reply click listener
        viewHolder.btnReply.setOnClickListener(new View.OnClickListener() {
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
        viewHolder.btnRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {

                    if (!tweet.isRetweeted()) {
                        client.postRetweet(tweet.getUid(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                viewHolder.btnRetweet.setBackgroundResource(R.drawable.retweet_action_on);
                                tweet.rtCount++;
                                viewHolder.tvRTCount.setText(String.valueOf(tweet.getRtCount()));
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
                                viewHolder.btnRetweet.setBackgroundResource(R.drawable.retweet_action);
                                tweet.rtCount--;
                                viewHolder.tvRTCount.setText(String.valueOf(tweet.getRtCount()));
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
        viewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {

                if (!tweet.isFavorited()) {
                    client.postFavorite(tweet.getUid(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            viewHolder.btnLike.setBackgroundResource(R.drawable.like_action_on);
                            tweet.likeCount++;
                            viewHolder.tvLikeCount.setText(String.valueOf(tweet.getLikeCount()));
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
                            viewHolder.btnLike.setBackgroundResource(R.drawable.like_action);
                            tweet.likeCount--;
                            viewHolder.tvLikeCount.setText(String.valueOf(tweet.getLikeCount()));
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
