package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginActionBarActivity;

import org.w3c.dom.Text;

//where the user will sign into twitter
public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

	Button btnLogIn;
	TextView welcome;
	TextView welcomeMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		welcome = (TextView) findViewById(R.id.tvWelcome);
		welcomeMessage = (TextView) findViewById(R.id.tvWelcomeMessage);
		btnLogIn = (Button) findViewById(R.id.btnLogin);
		Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/GothamNBold.otf");
		Typeface tf2 = Typeface.createFromAsset(getAssets(),"fonts/GothamNBook.otf");

		btnLogIn.setTypeface(tf);
		welcomeMessage.setTypeface(tf2);
		welcome.setTypeface(tf2);

	}


	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
		 Intent i = new Intent(this, TimelineActivity.class);
		 startActivity(i);
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		getClient().connect();
	}

}
