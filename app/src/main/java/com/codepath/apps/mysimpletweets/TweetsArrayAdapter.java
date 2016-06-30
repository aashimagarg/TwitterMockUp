package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static com.squareup.picasso.Picasso.*;

/**
 * Created by aashimagarg on 6/28/16.
 */
//Taking the tweet objects and turning them into Views that will be displayed in lists
public class TweetsArrayAdapter extends ArrayAdapter<Tweet>{

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

        //set on image click listener
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ivProfileImage != null) {
                    // Fire an intent when a contact is selected
                    Intent i = new Intent(v.getContext(), ProfileActivity.class);
                    // Pass contact object in the bundle and populate details activity.
                    i.putExtra("screen_name", tweet.getUser().getScreenName());
                    v.getContext().startActivity(i);
                }
            }
        });

        //5. return the view to be inserted into the list
        return convertView;
    }
}
