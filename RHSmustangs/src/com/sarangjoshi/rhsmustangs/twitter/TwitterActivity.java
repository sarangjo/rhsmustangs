package com.sarangjoshi.rhsmustangs.twitter;

import java.util.ArrayList;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import com.sarangjoshi.rhsmustangs.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TwitterActivity extends Activity {
	Button loginButton, showTweetsButton, logoutButton;
	TextView usernameText;
	WebView loginWebView;
	ListView tweetsList;
	
	enum TwitterStage { LOGGED_OUT, LOGGED_IN, SHOW_TWEETS };
	TwitterStage appTwitterStage = TwitterStage.LOGGED_OUT;
	
	ArrayList<String> tweets = new ArrayList<String>();
	ArrayAdapter tweetsAdapter;
	
	private TwitterAuthorization tAuth;
	private TwitterDataParse tData;
	
	public static Twitter twitter;

	public static final String REDMONDASB_USERNAME = "RedmondASB";
	
	private String webUrl = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_login);

		tAuth = new TwitterAuthorization(this);
		tData = new TwitterDataParse(this);
		
		setupViewVariables();

		updateViews();
		
		setupClickListeners();
		
		loginWebView.setWebViewClient(new WatcherWebClient());
	}
	
	/**
	 * Sets up the individual click listeners for the buttons.
	 */
	private void setupClickListeners() {
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (tAuth.tNetwork.isConnectedToInternet() && !tAuth.isTwitterLoggedIn())
					startAppAuthorization();
				else {
					Toast t = Toast.makeText(TwitterActivity.this,
							"Not connected to Internet.", Toast.LENGTH_LONG);
					t.show();
				}
			}
		});
		showTweetsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setContentView(R.layout.twitter_tweets);
				tweetsList = (ListView) findViewById(R.id.tweetsListView);
				loadTweets();
			}
			
		});

		logoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(tAuth.isTwitterLoggedIn()) {
					logoutFromTwitter();
					updateViews();
				}
			}
			
		});
	}
	
	/**
	 * Logs out of Twitter.
	 */

	private void logoutFromTwitter() {
		tAuth.logout();
		appTwitterStage = TwitterStage.LOGGED_OUT;
	}

	/**
	 * The function to get Tweets from the Data Parser and load them into a ListView.
	 */
	private void loadTweets() {
		tweets = tData.getTweets();
		
		tweetsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tweets);
		tweetsList.setAdapter(tweetsAdapter);
	}

	/**
	 * This sets up the views depending on whether the user is logged in or not.
	 */
	private void updateViews() {
		if (tAuth.isTwitterLoggedIn()){
			appTwitterStage = TwitterStage.LOGGED_IN;
			// GONE: Login Button
			loginButton.setVisibility(View.GONE);
			// VISIBLE: Username Text, Show Tweets, Logout
			usernameText.setVisibility(View.VISIBLE);
			showTweetsButton.setVisibility(View.VISIBLE);
			logoutButton.setVisibility(View.VISIBLE);
			
			usernameText.setText("Welcome " + tAuth.getUserId() + "!");
		} else {
			appTwitterStage = TwitterStage.LOGGED_OUT;
			// GONE: Show Tweets Button, Username Text, Logout
			showTweetsButton.setVisibility(View.GONE);
			usernameText.setVisibility(View.GONE);
			logoutButton.setVisibility(View.GONE);
			// VISIBLE: 
			loginButton.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Initializes all the view variables.
	 */
	private void setupViewVariables() {
		loginButton = (Button) findViewById(R.id.loginTwitterBtn);
		usernameText = (TextView) findViewById(R.id.usernameText);
		loginWebView = (WebView) findViewById(R.id.loginWebView);
		showTweetsButton = (Button) findViewById(R.id.showTweetsBtn);
		tweetsList = (ListView) findViewById(R.id.tweetsListView);
		logoutButton = (Button) findViewById(R.id.logoutBtn);
	}

	/**
	 * Starts the authorization process by showing the Web View with the login
	 * screen.
	 */
	private void startAppAuthorization() {
		// Shows Web view
		loginWebView.setVisibility(View.VISIBLE);
		// Hides other views
		loginButton.setVisibility(View.GONE);
		usernameText.setVisibility(View.GONE);
		showTweetsButton.setVisibility(View.GONE);

		// Load the authorization URL
		webUrl = tAuth.getAppAuthorizeUrl();

		loginWebView.loadUrl(webUrl);

		// At this point, waits for the User to load the callback URL.
	}

	/**
	 * Once the user has authorized the app, this method extracts the user's
	 * access token from the token and verifier.
	 * 
	 * @param url the authorization URL
	 */
	private void userAuthorization(String url) {
		// We are done with the WebView.
		loginWebView.setVisibility(View.GONE);
		loginButton.setVisibility(View.GONE);
		usernameText.setVisibility(View.VISIBLE);

		// Obtaining the user's access token + secret
		tAuth.setUserAuth(url);

		// User has logged in!
		this.usernameText.setText(tAuth.getUsername());
	}

	private class WatcherWebClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if (url.contains(TwitterAuthorization.TWITTER_CALLBACK_TAG)) {
				loginWebView.setVisibility(View.GONE);
			}
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains(TwitterAuthorization.TWITTER_CALLBACK_TAG)) {
				userAuthorization(url);

				return true;
			}
			return false;

		}
	}
}
