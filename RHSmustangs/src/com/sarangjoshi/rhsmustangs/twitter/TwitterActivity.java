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
	Button loginButton, showTweetsButton;
	TextView usernameText;
	WebView loginWebView;
	ListView tweetsList;
	
	ArrayAdapter tweetsAdapter;
	
	ArrayList<String> tweets;

	private TwitterAuthorization tAuth;
	private TwitterDataParse tData;

	public static final String REDMONDASB_USERNAME = "RedmondASB";
	
	private String webUrl = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_login);

		tAuth = new TwitterAuthorization(this);
		tData = new TwitterDataParse(this);
		tweetsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tweets);

		setViewVariables();

		setupViews();
		
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
				setContentView(R.layout.activity_twitter);
				loadTweets();
			}
			
		});
		loginWebView.setWebViewClient(new WatcherWebClient());
	}

	private void loadTweets() {
		
		
		tweetsList.setAdapter(tweetsAdapter);
	}

	/**
	 * This sets up the views depending on whether the user is logged in or not.
	 */
	private void setupViews() {
		// First, check to see if the user is logged in.
		// If the user is logged in, then display the username and a welcome sign.
		if (tAuth.isTwitterLoggedIn()){
			loginButton.setVisibility(View.GONE);
			usernameText.setText("Welcome " + tAuth.getUserId() + "!");
		} else {
			loginButton.setVisibility(View.VISIBLE);
			usernameText.setVisibility(View.GONE);
		}
		// If not, then display the Please Log In and button
	}

	/**
	 * Initializes all the view variables.
	 */
	private void setViewVariables() {
		loginButton = (Button) findViewById(R.id.loginTwitterBtn);
		usernameText = (TextView) findViewById(R.id.usernameText);
		loginWebView = (WebView) findViewById(R.id.loginWebView);
		showTweetsButton = (Button) findViewById(R.id.showTweetsBtn);
		tweetsList = (ListView) findViewById(R.id.tweetsListView);
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
