package com.sarangjoshi.rhsmustangs.twitter;

import java.util.concurrent.ExecutionException;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class TwitterNetwork {
	private Context context;

	public TwitterNetwork(Context newContext) {
		context = newContext;
	}

	/**
	 * Checks for connection.
	 * 
	 * @return if it is connected to the internet
	 */
	public boolean isConnectedToInternet() {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager != null) {
			NetworkInfo[] info = manager.getAllNetworkInfo();

			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public RequestToken getRequestToken(String callbackUrl) {
		try {
			return new GetRequestTokenTask().execute(callbackUrl).get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	/**
	 * Gets the app Request Token, given a callback URL. 
	 */
	private class GetRequestTokenTask extends
			AsyncTask<String, Void, RequestToken> {
		ProgressDialog pDialog;

		@Override
		public void onPreExecute() {
			pDialog = ProgressDialog.show(context, "",
					"Connecting to Twitter...");
		}

		@Override
		protected RequestToken doInBackground(String... params) {
			RequestToken rToken = null;

			try {
				String callbackUrl = params[0];
				rToken = TwitterActivity.twitter
						.getOAuthRequestToken(callbackUrl);
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return rToken;
		}

		@Override
		public void onPostExecute(RequestToken result) {
			pDialog.dismiss();
		}
	}

	public AccessToken getAccessToken(String verifier) {
		try {
			return new GetAccessTokenTask().execute(verifier).get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	/**
	 * Gets the user Access Token, given a verifier. Accesses the static
	 * RequestToken in TwitterAuthorization.
	 * 
	 */
	private class GetAccessTokenTask extends
			AsyncTask<String, Void, AccessToken> {
		ProgressDialog pDialog;

		public void onPreExecute() {
			pDialog = ProgressDialog.show(context, "",
					"Getting Access Token...");
		}

		@Override
		protected AccessToken doInBackground(String... params) {
			String verifier = params[0];

			AccessToken aToken = null;
			try {
				aToken = TwitterActivity.twitter.getOAuthAccessToken(
						TwitterAuthorization.appRequestToken, verifier);
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
			}
			return aToken;
		}

		public void onPostExecute(AccessToken result) {
			pDialog.dismiss();
			TwitterAuthorization.userAccessToken = result;
		}
	}
	
	public ResponseList<twitter4j.Status> getTweets() {
		try {
			return new GetTweetsTask().execute().get();
		} catch (Exception e) {
			return null;
		}
	}
	private class GetTweetsTask extends AsyncTask<Void, Void, ResponseList<twitter4j.Status>> {

		@Override
		protected ResponseList<twitter4j.Status> doInBackground(Void... arg0) {
			ResponseList<twitter4j.Status> statuses = null;
			
			try {
				statuses = TwitterActivity.twitter.getUserTimeline(TwitterActivity.REDMONDASB_USERNAME);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// TODO Auto-generated method stub
			return statuses;
		}		
	}
}