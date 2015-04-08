/**
 * TwitterNetwork.java
 * 10 Apr 14
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.twitter;

import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class TNetwork {
	private Context context;

	public TNetwork(Context newContext) {
		context = newContext;
	}

	/**
	 * Gets the app Request Token, given a callback URL.
	 * 
	 * @param callbackUrl
	 *            the callback URL
	 * @return the app's request token
	 */
	public RequestToken getRequestToken(String callbackUrl) {
		RequestToken rToken = TAuthorization.appRequestToken;

		try {
			rToken = TActivity.twitter.getOAuthRequestToken(callbackUrl);
		} catch (Exception e) {
		}

		return rToken;
	}

	public AccessToken getAccessToken(String verifier) {
		try {
			return new GetAccessTokenTask().execute(verifier).get();
		} catch (Exception e) {
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
				aToken = TActivity.twitter.getOAuthAccessToken(
						TAuthorization.appRequestToken, verifier);
			} catch (TwitterException e) {
			}
			return aToken;
		}

		public void onPostExecute(AccessToken result) {
			pDialog.dismiss();
			TAuthorization.userAccessToken = result;
		}
	}

	public ResponseList<twitter4j.Status> getTweets() {
		try {
			return new GetTweetsTask().execute().get();
		} catch (Exception e) {
			return null;
		}
	}

	private class GetTweetsTask extends
			AsyncTask<Void, Void, ResponseList<twitter4j.Status>> {
		ProgressDialog pDialog;

		public void onPreExecute() {
			pDialog = ProgressDialog.show(context, "", "Getting tweets...");
		}

		@Override
		protected ResponseList<twitter4j.Status> doInBackground(Void... arg0) {
			ResponseList<twitter4j.Status> statuses = null;

			try {
				statuses = TActivity.twitter
						.getUserTimeline(TActivity.REDMONDASB_USERNAME);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return statuses;
		}

		public void onPostExecute(ResponseList<twitter4j.Status> result) {
			pDialog.dismiss();
		}
	}

	public String getUserName() {
		try {
			return new GetUserNameTask().execute().get();
		} catch (Exception e) {
			return null;
		}
	}

	private class GetUserNameTask extends AsyncTask<Void, Void, String> {
		ProgressDialog pDialog;

		public void onPreExecute() {
			pDialog = ProgressDialog.show(context, "", "Getting username...");
		}

		@Override
		protected String doInBackground(Void... params) {
			String s = "";
			try {
				s = TActivity.twitter.getScreenName();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return s;
		}

		public void onPostExecute(String result) {
			pDialog.dismiss();
		}
	}

	/**
	 * Favorites the given tweet id.
	 * 
	 * @param tweetId
	 *            a particular tweet's unique ID.
	 * @return the favorited status
	 */
	public twitter4j.Status favorite(long tweetId) {
		try {
			return new FavoriteTweetTask().execute(tweetId).get();
		} catch (Exception e) {
			return null;
		}
	}

	private class FavoriteTweetTask extends
			AsyncTask<Long, Void, twitter4j.Status> {
		ProgressDialog pDialog;

		public void onPreExecute() {
			pDialog = ProgressDialog.show(context, "", "Favoriting...");
		}

		@Override
		protected twitter4j.Status doInBackground(Long... tweetId) {
			try {
				// TODO: Solve this shit
				twitter4j.Status status = TActivity.twitter
						.createFavorite(tweetId[0].longValue());
				return status;
			} catch (TwitterException e) {
				e.printStackTrace();
				return null;
			}
		}

		public void onPostExecute(twitter4j.Status result) {
			pDialog.dismiss();
		}
	}

	/**
	 * Unavorites the given tweet id.
	 * 
	 * @param tweetId
	 *            a particular tweet's unique ID.
	 * @return the unfavorited status
	 */
	public twitter4j.Status unfavorite(long tweetId) {
		try {
			return new UnfavoriteTweetTask().execute(tweetId).get();
		} catch (Exception e) {
			return null;
		}
	}

	private class UnfavoriteTweetTask extends
			AsyncTask<Long, Void, twitter4j.Status> {
		ProgressDialog pDialog;

		public void onPreExecute() {
			pDialog = ProgressDialog.show(context, "", "Favoriting...");
		}

		@Override
		protected twitter4j.Status doInBackground(Long... tweetId) {
			try {
				// TODO: Solve this shit
				twitter4j.Status status = TActivity.twitter
						.destroyFavorite(tweetId[0].longValue());
				return status;
			} catch (TwitterException e) {
				e.printStackTrace();
				return null;
			}
		}

		public void onPostExecute(twitter4j.Status result) {
			pDialog.dismiss();
		}
	}
}