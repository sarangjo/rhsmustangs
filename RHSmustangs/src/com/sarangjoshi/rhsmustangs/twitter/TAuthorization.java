package com.sarangjoshi.rhsmustangs.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class TAuthorization {
	private SharedPreferences mySP;
	private Context context;

	// API keys, specific to my application
	static String APP_KEY = "K5q84hY7cvjZKUQWyWXMw";
	static String APP_SECRET = "xqPFIYMH6blTDD3twejIfqfXD69OZJdzar10ezfh4g";

	// Preference Constants
	static String preferenceName = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLoggedIn";
	static final String PREF_FILE_NAME = "TwitterPref";
	
	static final String TWITTER_CALLBACK_TAG = "rhsmustangs";
	static final String TWITTER_CALLBACK_URL = "http://" + TWITTER_CALLBACK_TAG
			+ ".com";

	// OAuth keys, specific to the user
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

	public static String LOGOUT_URL = "https://mobile.twitter.com/logout";
	

	public static RequestToken appRequestToken;
	public static AccessToken userAccessToken;
	public User user;

	private String authUrl;

	public String getAuthUrl() {
		return authUrl;
	}

	public TNetwork tNetwork;

	public TAuthorization(Context newContext) {
		context = newContext;

		tNetwork = new TNetwork(newContext);
		mySP = context.getSharedPreferences(PREF_FILE_NAME, 0);

	}

	public boolean areApiKeysValid() {
		return !(APP_KEY.trim().length() == 0 || APP_SECRET.trim().length() == 0);
	}

	/**
	 * Sets up the Twitter configuration.
	 */
	public void setupTwitterLogin() {
		if (isTwitterLoggedIn()) {
			// Credentials are already available in the form of the AccessToken
			TActivity.twitter = TwitterFactory.getSingleton();
			try {
				// First, sets the app details, in case they haven't been set
				// yet.
				TActivity.twitter.setOAuthConsumer(APP_KEY, APP_SECRET);
			} catch (IllegalStateException e) {
				// OAuth already set
				// OR
				// Instance using basic auth
			}
			// Gets previously stored user OAuth details
			String oauth_token = mySP.getString(PREF_KEY_OAUTH_TOKEN, "");
			String oauth_secret = mySP.getString(PREF_KEY_OAUTH_SECRET, "");
			userAccessToken = new AccessToken(oauth_token, oauth_secret);
			TActivity.twitter.setOAuthAccessToken(userAccessToken);
		} else {
			// Configures a Twitter login setup with the App's key and
			// secret.
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true).setOAuthConsumerKey(APP_KEY)
					.setOAuthConsumerSecret(APP_SECRET);
			TwitterFactory tf = new TwitterFactory(cb.build());
			TActivity.twitter = tf.getInstance();
		}
	}

	/**
	 * Gets the URL to open for authorizing the user. This URL is specific to
	 * the app, not the user.
	 * 
	 * @return the authorization URL to open for the user.
	 */
	public String getAppAuthorizeUrl() {
		Editor edit = mySP.edit();
		edit.putBoolean(PREF_KEY_TWITTER_LOGIN, false);

		edit.commit();

		if (!isTwitterLoggedIn()) {
			// Alternate Twitter setup style
			/*
			 * twitter = TwitterFactory.getSingleton();
			 * twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			 */

			try {
				// Calls TwitterNetwork to get an app Request Token, with the
				// given callback URL.
				appRequestToken = tNetwork
						.getRequestToken(TWITTER_CALLBACK_URL);
				authUrl = appRequestToken.getAuthorizationURL();
				return authUrl;
			} catch (Exception e) {
				// If there is an exception in the process of obtaining App
				// Request Token
			}
		} else {
			Toast x = Toast.makeText(context.getApplicationContext(),
					"Already logged into Twitter", Toast.LENGTH_LONG);
			x.show();
		}

		return null;
	}

	/**
	 * Given the authorization URL.
	 * 
	 * @param url
	 *            the URL navigated to, containing the access token and verifier
	 *            strings
	 * 
	 */
	public void setUserAuth(String url) {
		if (!isTwitterLoggedIn()) {
			Uri uri = Uri.parse(url);
			// First of all, ensures that the provided URL has the callback tag
			// - this ensures that we are on the right track.
			if (url != null && url.toString().contains(TWITTER_CALLBACK_TAG)) {
				// From the URL, parsing out the verifier string.
				String verifier = uri
						.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

				userAccessToken = tNetwork.getAccessToken(verifier);

				Editor editor = mySP.edit();
				// The user's access token.
				editor.putString(PREF_KEY_OAUTH_TOKEN,
						userAccessToken.getToken());
				// The user's access token secret. Together with the access
				// token, this is the user's identification, without having to
				// reveal his/her password.
				editor.putString(PREF_KEY_OAUTH_SECRET,
						userAccessToken.getTokenSecret());
				editor.putBoolean(PREF_KEY_TWITTER_LOGIN, true);

				// Actually stores the values to the file.
				editor.commit();

				Log.i("Twitter OAuth Token", "> " + userAccessToken.getToken());
			}
		}
	}

	/**
	 * Accesses the SharedPreferences file and extracts whether the user has
	 * logged into Twitter or not.
	 * 
	 * @return if the user is logged into Twitter
	 */
	public boolean isTwitterLoggedIn() {
		boolean isL = mySP.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
		return isL;
	}

	public long getUserId() {
		return userAccessToken.getUserId();
	}

	public String getUsername() {
		return tNetwork.getUserName();
	}

	/**
	 * Resets all the SharedPreference details.
	 */
	public void reset() {
		Editor edit = mySP.edit();
		edit.putBoolean(PREF_KEY_TWITTER_LOGIN, false);
		edit.commit();
	}

	/**
	 * Deletes all the SharedPreferences data, and nullifies the AccessToken.
	 */
	public void logout() {
		Editor edit = mySP.edit();
		edit.putString(PREF_KEY_OAUTH_TOKEN, "");
		edit.putString(PREF_KEY_OAUTH_SECRET, "");
		edit.putBoolean(PREF_KEY_TWITTER_LOGIN, false);
		edit.commit();

		userAccessToken = null;
		appRequestToken = null;
		TActivity.twitter = null;
	}
}
