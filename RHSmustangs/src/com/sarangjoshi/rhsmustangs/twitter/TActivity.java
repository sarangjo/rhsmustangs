/**
 * TwitterActivity.java
 * Apr 1, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.twitter;

import twitter4j.Twitter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sarangjoshi.rhsmustangs.Network;
import com.sarangjoshi.rhsmustangs.R;

public class TActivity extends Activity {
	Button loginButton, showTweetsButton, logoutButton;
	TextView usernameText;
	WebView webView;

	enum TwitterStage {
		LOGGED_OUT, LOGGING_IN, LOGGED_IN, SHOW_TWEETS, LOGGING_OUT
	};

	public static TwitterStage appTwitterStage = TwitterStage.LOGGED_OUT;

	private TAuthorization tAuth;
	public static Twitter twitter;

	public static final String REDMONDASB_USERNAME = "RedmondASB";

	private String webUrl = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitter_login);

		setupVariables();

		tAuth = new TAuthorization(this);
		new TDataParse(this);

		setupClickListeners();
	}

	@Override
	public void onStart() {
		super.onStart();

		new SetupTwitterTask().execute();
	}

	/**
	 * Initializes all the view variables.
	 */
	private void setupVariables() {
		loginButton = (Button) findViewById(R.id.loginTwitterBtn);
		usernameText = (TextView) findViewById(R.id.usernameText);
		webView = (WebView) findViewById(R.id.loginWebView);
		showTweetsButton = (Button) findViewById(R.id.showTweetsBtn);
		logoutButton = (Button) findViewById(R.id.logoutBtn);
	}

	/**
	 * Sets up the individual click listeners for the buttons.
	 */
	private void setupClickListeners() {
		// Login button
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Network.isConnectedToInternet(TActivity.this))
					if (!tAuth.isTwitterLoggedIn())
						loginToTwitter();
					else {
						Toast.makeText(TActivity.this, "Already logged in.",
								Toast.LENGTH_LONG).show();
					}
				else {
					Toast.makeText(TActivity.this,
							"Not connected to Internet.", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		showTweetsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showTweets();
			}

		});
		logoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tAuth.isTwitterLoggedIn()) {
					logoutFromTwitter();
					// logoutPart2();
				} else {
					Toast.makeText(TActivity.this, "Already logged out.",
							Toast.LENGTH_SHORT).show();
				}
			}

		});
	}

	/**
	 * Starts the activity to show the tweets.
	 */
	private void showTweets() {
		if (tAuth.isTwitterLoggedIn())
			startActivity(new Intent(TActivity.this, TTweetsActivity.class));
		else {
			Toast.makeText(this, "Please login first.", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * Logs out of Twitter.
	 */
	private void logoutFromTwitter() {
		// Step 1: Logout from the Web View.
		appTwitterStage = TwitterStage.LOGGING_OUT;

		// Attaching Logout WebViewClient to monitor when the logout button was
		// clicked.
		webView.setWebViewClient(new LogoutWebClient());

		updateViews();
		// Load the logout URL (stored in TwitterAuthorization)
		webView.loadUrl(TAuthorization.LOGOUT_URL);
	}

	/**
	 * After the WebView has registered the logout button click.
	 */
	private void logoutPart2() {
		tAuth.logout();
		appTwitterStage = TwitterStage.LOGGED_OUT;
		updateViews();
	}

	/**
	 * This sets up the views depending on whether the user is logged in or not.
	 */
	private void updateViews() {
		switch (appTwitterStage) {
		case LOGGED_IN:
			setViewsVis(View.VISIBLE, usernameText, showTweetsButton,
					logoutButton, loginButton);
			setViewsVis(View.GONE, webView);
			usernameText.setText("Welcome " + tAuth.getUsername() + "!");
			break;
		case LOGGED_OUT:
			setViewsVis(View.VISIBLE, loginButton, logoutButton);
			setViewsVis(View.GONE, showTweetsButton, usernameText, webView);
			break;
		case LOGGING_IN:
			setViewsVis(View.VISIBLE, webView);
			setViewsVis(View.GONE, showTweetsButton, usernameText,
					logoutButton, loginButton);
			break;
		case LOGGING_OUT:
			setViewsVis(View.VISIBLE, webView);
			setViewsVis(View.GONE, showTweetsButton, usernameText,
					logoutButton, loginButton);
			break;
		default:
			break;
		}
	}

	/**
	 * Sets the visibility of the given collection of views to the given
	 * visibility.
	 * 
	 * @param visibility
	 *            chosen visibility for all the views
	 * @param views
	 *            all the views to have the visibility set
	 */
	private void setViewsVis(int visibility, View... views) {
		for (int i = 0; i < views.length; i++) {
			views[i].setVisibility(visibility);
		}
	}

	/**
	 * Starts the authorization process by showing the Web View with the login
	 * screen.
	 */
	private void loginToTwitter() {
		// Attach Login WebViewClient to the WebView.
		webView.setWebViewClient(new LoginWebClient());

		appTwitterStage = TwitterStage.LOGGING_IN;
		updateViews();

		tAuth.setupTwitterLogin();

		// Load the authorization URL
		webUrl = tAuth.getAppAuthorizeUrl();

		webView.loadUrl(webUrl);

		// At this point, waits for the User to load the callback URL.
	}

	/**
	 * Once the user has authorized the app, this method extracts the user's
	 * access token from the token and verifier.
	 * 
	 * @param url
	 *            the authorization URL
	 */
	private void loginPart2Auth(String url) {
		// We are done with the WebView.
		webView.setVisibility(View.GONE);

		// Obtaining the user's access token + secret
		tAuth.setUserAuth(url);

		if (tAuth.isTwitterLoggedIn())
			appTwitterStage = TwitterStage.LOGGED_IN;

		updateViews();
	}

	// WEB CLIENTS
	private class LoginWebClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			/*
			 * if (url.equals("https://api.twitter.com/oauth/authorize")) {
			 * appTwitterStage = TwitterStage.LOGGED_OUT; updateViews();
			 * ((WebView) view).stopLoading(); }
			 */
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains(TAuthorization.TWITTER_CALLBACK_TAG)) {
				loginPart2Auth(url);

				return true;
			}
			return false;

		}
	}

	private class LogoutWebClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if (!url.contains("logout")) {
				logoutPart2();

			}
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (!url.contains("logout")) {
				logoutPart2();

				return true;
			}

			return false;
		}
	}

	// ASYNCTASKS
	private class SetupTwitterTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;

		protected void onPreExecute() {
			pd = ProgressDialog.show(TActivity.this, "", "Loading...");
		}

		@Override
		protected Void doInBackground(Void... params) {
			tAuth.setupTwitterLogin();

			if (tAuth.isTwitterLoggedIn())
				appTwitterStage = TwitterStage.LOGGED_IN;

			return null;
		}

		protected void onPostExecute(Void result) {
			updateViews();

			pd.dismiss();
		}

	}

	private class SetupLoginTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
