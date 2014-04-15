package com.sarangjoshi.rhsmustangs.twitter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.message.*;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sarangjoshi.rhsmustangs.AlertDialogManager;
import com.sarangjoshi.rhsmustangs.R;
import com.sarangjoshi.rhsmustangs.R.id;
import com.sarangjoshi.rhsmustangs.R.layout;
import com.sarangjoshi.rhsmustangs.R.string;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;

public class TwitterActivity_Old extends Activity {
	ArrayList<Tweet> tweets = new ArrayList<Tweet>();
	TweetListAdapter adapter;
	ListView tweetView;

	// API keys, specific to my application
	static String CONSUMER_KEY = "K5q84hY7cvjZKUQWyWXMw";
	static String CONSUMER_SECRET = "xqPFIYMH6blTDD3twejIfqfXD69OZJdzar10ezfh4g";

	// Preference Constants
	static String preferenceName = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLoggedIn";

	static final String TWITTER_CALLBACK_TAG = "rhsmustangs";

	// OAuth keys, specific to the user
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

	Button loginTwitterButton;
	TextView userNameView;
	WebView webView;

	AlertDialogManager adManager;
	//ConnectionDetector cd;

	private Twitter twitter;
	private RequestToken requestToken;

	SharedPreferences sp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Sets the content view
		setContentView(R.layout.twitter_login);

		// Helper objects
		//cd = new ConnectionDetector(getApplicationContext());
		adManager = new AlertDialogManager();

		// This is essentially a Map of keys to values that will hold the auth
		// details for the user.
		sp = getApplicationContext().getSharedPreferences("MyPref", 0);

		// Check Internet connection
		/*if (!cd.isConnected()) {
			adManager.showAlertDialog(this, "Internet Error",
					"Unable to connect to internet.");

			return;
		}*/

		// Check validity of API keys
		if (CONSUMER_KEY.trim().length() == 0
				|| CONSUMER_SECRET.trim().length() == 0) {
			adManager
					.showAlertDialog(this, "API Key Error", "Check your keys.");

			return;
		}

		// Views
		loginTwitterButton = (Button) findViewById(R.id.loginTwitter);
		userNameView = (TextView) findViewById(R.id.usernameText);
		webView = (WebView) findViewById(R.id.loginWebView);

		webView.setWebViewClient(new MyWebViewClient());

		loginTwitterButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					new GetTwitterAuthTask().execute();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});

		// tweetView = (ListView) this.findViewById(R.id.tweetsListView);

		// new JSONRequestTask().execute();
	}

	private String authUrl = "";

	private void loginButtonClicked(View v) {
		try {
			new GetTwitterAuthTask().execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if (url.contains(TWITTER_CALLBACK_TAG)) {
				webView.setVisibility(View.GONE);

				doneAuthenticating(url);
			}
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains(TWITTER_CALLBACK_TAG)) {
				webView.setVisibility(View.GONE);

				doneAuthenticating(url);

				return true;
			}
			return false;

		}
	}

	/**
	 * Given the authorization URL, makes the web view visible and loads the
	 * authorization URL.
	 * 
	 * @param authUrl
	 *            the authorization URL obtained from the app API key.
	 */
	private void showWebViewAndLogin(String authUrl) {
		webView.setVisibility(View.VISIBLE);
		loginTwitterButton.setVisibility(View.GONE);
		userNameView.setVisibility(View.GONE);

		webView.loadUrl(authUrl);
	}

	private void doneAuthenticating(String loadedUrl) {
		if (!isTwitterLoggedInAlready()) {
			Uri uri = Uri.parse(loadedUrl);
			if (loadedUrl != null
					&& loadedUrl.toString().contains(TWITTER_CALLBACK_TAG)) {
				String verifier = uri
						.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

				try {
					new AuthGetUserIdTask().execute(verifier);
				} catch (Exception e) {
					Log.e("Twitter Login Error", "> " + e.getMessage());
				}
			}

		}
	}

	private class AuthGetUserIdTask extends AsyncTask<String, Void, Long> {
		ProgressDialog pDialog;

		@Override
		public void onPreExecute() {
			pDialog = ProgressDialog.show(TwitterActivity_Old.this, "",
					"AUTH3NTICATIN'");
		}

		@Override
		protected Long doInBackground(String... params) {
			String verifier = params[0];

			AccessToken aToken;
			try {
				aToken = twitter.getOAuthAccessToken(requestToken, verifier);

				Editor editor = sp.edit();

				editor.putString(PREF_KEY_OAUTH_TOKEN, aToken.getToken());
				editor.putString(PREF_KEY_OAUTH_SECRET, aToken.getTokenSecret());
				editor.putBoolean(PREF_KEY_TWITTER_LOGIN, true);

				editor.commit();

				Log.e("Twitter OAuth Token", "> " + aToken.getToken());

				long userID = aToken.getUserId();
				/*
				 * User user = twitter.showUser(userID); String username =
				 * user.getName();
				 * 
				 * userNameView.setText("Welcome " + username + "!");
				 */

				return userID;
			} catch (TwitterException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return (long) -1;
		}

		@Override
		protected void onPostExecute(Long result) {
			pDialog.dismiss();
		}
	}

	private boolean isTwitterLoggedInAlready() {
		return sp.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}

	private class GetTwitterAuthTask extends AsyncTask<Void, Void, String> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			pDialog = ProgressDialog.show(TwitterActivity_Old.this, "Loading",
					"TW33TIN'", true);
		}

		@Override
		protected String doInBackground(Void... arg0) {
			String authUrl = "";

			if (!isTwitterLoggedInAlready()) {
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(CONSUMER_KEY);
				builder.setOAuthConsumerSecret(CONSUMER_SECRET);
				Configuration configuration = builder.build();

				TwitterFactory factory = new TwitterFactory(configuration);
				twitter = factory.getInstance();

				/*
				 * twitter = TwitterFactory.getSingleton();
				 * twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
				 */

				try {
					requestToken = twitter.getOAuthRequestToken("http://"
							+ TWITTER_CALLBACK_TAG + ".com");
					authUrl = requestToken.getAuthenticationURL();
					// TwitterActivity.this.startActivity(new
					// Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));

				} catch (TwitterException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						"Already logged into Twitter", Toast.LENGTH_LONG);
			}

			return authUrl;
		}

		@Override
		protected void onPostExecute(String result) {
			pDialog.dismiss();

			authUrl = result;
			showWebViewAndLogin(authUrl);
		}
	}

	/**
	 * An AsyncTask that pulls tweets that will then be loaded into tweetView.
	 */
	private class JSONRequestTask extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progressDialog;
		public RequestQueue queue;

		/**
		 * Shows a progress dialog which says "Loading".
		 */
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(TwitterActivity_Old.this, "",
					"Go pee or something...", true);

			queue = Volley.newRequestQueue(TwitterActivity_Old.this);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				String endUrl = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=redmondasb&count=2";

				appOnlyAuth(endUrl);

				/*
				 * String baseURL =
				 * "https://api.twitter.com/1.1/search/tweets.json"; String q =
				 * "android";
				 * 
				 * HttpClient hClient = new DefaultHttpClient(); // Base URL for
				 * the GET request. // As of now, GET requesting all tweets with
				 * the query // "android". HttpGet hGet = new HttpGet(baseURL);
				 * 
				 * // Actual HTTP response from the GET HttpResponse hResponse =
				 * hClient.execute(hGet); // Status: 200 is OK StatusLine
				 * hStatus = hResponse.getStatusLine();
				 * 
				 * if (hStatus.getStatusCode() == HttpStatus.SC_OK) { String
				 * result = EntityUtils.toString(hResponse.getEntity());
				 * JSONObject root = new JSONObject(result); JSONArray sessions
				 * = root.getJSONArray("results");
				 * 
				 * for (int i = 0; i < sessions.length(); i++) { JSONObject
				 * session = sessions.getJSONObject(i);
				 * 
				 * Tweet tweet = new Tweet(); tweet.content =
				 * session.getString("text"); tweet.author =
				 * session.getString("screen_name"); tweets.add(tweet); } }
				 */
			} catch (Exception e) {
				Log.e("TwitterActivity", "Error loading JSON", e);
			}
			return null;
		}

		private String authParam(String name, String param, boolean isLast) {
			return name + "=\"" + param + "\"" + ((!isLast) ? ", " : "");
		}

		private String param(String name, String param, boolean isFirst) {
			return ((!isFirst) ? "&" : "") + name + "="
					+ URLEncoder.encode(param);
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
			tweetView.setAdapter(new TweetListAdapter(TwitterActivity_Old.this,
					R.layout.layout_tweet, tweets));
		}

		public void oauth(String baseURL, String q, HttpGet hGet) {
			// Creating the headers for the authentication
			hGet.setHeader("Accept", "*/*");
			hGet.setHeader("Connection", "close");
			hGet.setHeader("User-Agent", "OAuth gem v0.4.4");
			hGet.setHeader("Content-Type", "application/x-www-form-urlencoded");

			// Getting oauth_signature
			//
			// OAuth Authentication details
			// https://dev.twitter.com/docs/auth/authorizing-request

			String oauth_consumer_key = "K5q84hY7cvjZKUQWyWXMw";
			String oauth_nonce = "kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg";
			String oauth_signature_method = "HMAC-SHA1";
			String oauth_timestamp = "" + System.currentTimeMillis();
			String oauth_token = "498226494-F8kIC2ykNTpZkFxSmZxlS2809gYvS30uZPm4gH8i";
			String oauth_version = "1.0";

			// Getting signature
			String sigParams = "";
			sigParams += param("oauth_consumer_key", oauth_consumer_key, true);
			sigParams += param("oauth_nonce", oauth_nonce, false);
			sigParams += param("oauth_signature_method",
					oauth_signature_method, false);
			sigParams += param("oauth_timestamp", System.currentTimeMillis()
					+ "", false);
			sigParams += param("oauth_token", oauth_token, false);
			sigParams += param("oauth_version", oauth_version, false);
			sigParams += param("q", q, false);

			// Combining GET request and the parameters
			String SIG = "GET&" + URLEncoder.encode(baseURL) + "&";
			SIG += URLEncoder.encode(sigParams);

			// Getting HMAC-SHA1 key
			String consumerSecret = "xqPFIYMH6blTDD3twejIfqfXD69OZJdzar10ezfh4g";
			String oAuthTokenSecret = "BfNI2X1q80SDyOjLEXO75mYd6lGUv5VtYSdUNNk7lEMxn";
			String signingKey = URLEncoder.encode(consumerSecret) + "&"
					+ URLEncoder.encode(oAuthTokenSecret);

			// value: SIG
			// key: signingKey
			// result: oAuthSignature
			String oauth_signature = "";

			try {
				byte[] keyBytes = signingKey.getBytes();
				SecretKeySpec signingKeySpec = new SecretKeySpec(keyBytes,
						"HmacSHA1");
				Mac mac = Mac.getInstance("HmacSHA1");
				mac.init(signingKeySpec);

				// creating the raw hmac
				byte[] rawHmac = mac.doFinal(SIG.getBytes());

				// converting to base64
				oauth_signature = new String(new Base64().encode(rawHmac));
			} catch (Exception e) {
				Log.e("TwitterActivity", "Error with Signature", e);
			}

			// Getting DST value for final Authorization header
			String DST = "OAuth ";
			DST += authParam("oauth_consumer", oauth_consumer_key, false);
			DST += authParam("oauth_nonce", oauth_nonce, false);
			DST += authParam("oauth_signature", oauth_signature, false);
			DST += authParam("oauth_signature_method", oauth_signature_method,
					false);
			DST += authParam("oauth_timestamp", oauth_timestamp, false);
			DST += authParam("oauth_token", oauth_token, false);
			DST += authParam("oauth_version", oauth_version, true);

			// Adding authorization header
			hGet.setHeader("Authorization", DST);
		}

		public void appOnlyAuth(String endpointUrl) throws IOException {
			String appKey = "K5q84hY7cvjZKUQWyWXMw";
			String appSecret = "xqPFIYMH6blTDD3twejIfqfXD69OZJdzar10ezfh4g";

			encodedCredentials = encodeKeys(appKey, appSecret);

			getReqBearerToken();

			HttpsURLConnection connection = null;

			try {
				URL url = new URL(endpointUrl);
				connection = (HttpsURLConnection) url.openConnection();

				connection.setDoOutput(true);
				connection.setDoInput(true);

				connection.setRequestMethod("GET");
				connection.setRequestProperty("Host", "api.twitter.com");
				connection.setRequestProperty("User-Agent", getResources()
						.getString(R.string.app_name));
				connection.setRequestProperty("Authorization", "Bearer "
						+ bearerToken);
				connection.setUseCaches(false);

				JSONArray obj = (JSONArray) JSONValue
						.parse(readResponse(connection));

				if (obj != null) {
					try {
						int i = 0;
						// for (int i = 0; i < obj.length(); i++) {
						String text = ((JSONObject) obj.get(i)).get("text")
								.toString();
						tweets.add(new Tweet("RedmondASB", text));

						// }
					} catch (JSONException e) {
						throw new IOException("JSON error.", e);
					}
				}
			} catch (MalformedURLException e) {

			} finally {
				if (connection != null)
					connection.disconnect();
			}
		}

		String encodedCredentials = "";

		/**
		 * Gets request bearer token.
		 * 
		 * @param endUrl
		 * @return
		 * @throws IOException
		 */
		public String getReqBearerToken() throws IOException {
			HttpsURLConnection connection = null;

			try {
				URL url = new URL("https://api.twitter.com/oauth2/token");

				connection = (HttpsURLConnection) url.openConnection();

				connection.setInstanceFollowRedirects(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setInstanceFollowRedirects(false);
				connection.setRequestMethod("POST");
				connection.setInstanceFollowRedirects(false);
				connection.setDoOutput(true);

				// POST request properties
				connection.setRequestProperty("Host", "api.twitter.com");
				connection.setRequestProperty("User-Agent", getResources()
						.getString(R.string.app_name));
				connection.setRequestProperty("Authorization", "Basic "
						+ encodedCredentials);
				connection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded;charset=UTF-8");
				connection.setRequestProperty("Content-Length", "29");
				connection.setRequestProperty("Accept-Encoding", "gzip");
				connection.setUseCaches(false);

				// Attaches the main body to the request.
				writeRequest(connection, "grant_type=client_credentials");

				String response = readResponse(connection);

				JSONObject obj = (JSONObject) JSONValue.parse(response);

				if (obj != null) {
					try {
						// .get gets values given a tag name.
						String tokenType;

						tokenType = (String) obj.get("token_type");
						String token = (String) obj.get("access_token");

						if (tokenType.equals("bearer") && token != null)
							return token;
						else
							return "";
					} catch (JSONException e) {
						throw new IOException("JSON error.", e);
					}
				}
				return new String();

			} catch (MalformedURLException e) {
				throw new IOException("Invalid URL specified.", e);
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		}

		public void getReqBearerTokenVolley() throws IOException {
			final String encodedCred = encodeKeys("K5q84hY7cvjZKUQWyWXMw",
					"xqPFIYMH6blTDD3twejIfqfXD69OZJdzar10ezfh4g");

			// JsonObjectRequest postRequest = new JsonObjectRequest(
			// Request.Method.POST, endUrl, null, getReqListener(),
			// getReqErrorListener()) {

			System.out.println(strObj);
		}

		ArrayList<NameValuePair> postReqHeaders = new ArrayList<NameValuePair>();
		HttpParams httpParams = new BasicHttpParams();

		public void getReqBearerTokenClient() {
			String reqBearUrl = "https://api.twitter.com/oauth2/token";

			HttpClient hc = new DefaultHttpClient();
			HttpPost hPost = new HttpPost(reqBearUrl);

			try {
				// ///// HEADERS ///////
				postReqHeaders.add(new BasicNameValuePair("Host",
						"api.twitter.com"));
				postReqHeaders.add(new BasicNameValuePair("User-Agent",
						getResources().getString(R.string.app_name)));
				postReqHeaders.add(new BasicNameValuePair("Authorization",
						"Basic " + encodedCredentials));
				postReqHeaders.add(new BasicNameValuePair("Content-Type",
						"application/x-www-form-urlencoded;charset=UTF-8"));
				// postReqHeaders.add(new BasicNameValuePair("Content-Length",
				// "29"));
				postReqHeaders.add(new BasicNameValuePair("Accept-Encoding",
						"gzip"));

				for (int i = 0; i < postReqHeaders.size(); i++) {
					hPost.setHeader(postReqHeaders.get(i).getName(),
							postReqHeaders.get(i).getValue());
				}

				// ///// PARAMS ///////
				ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("grant_type",
						"client_credentials"));

				httpParams.setParameter("grant_type", "client_credentials");

				// hPost.setParams(httpParams);
				hPost.setEntity(new UrlEncodedFormEntity(params));

				HttpResponse response = hc.execute(hPost);
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				String line = rd.readLine();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private String bearerToken;
		private JSONObject obj;
		private String strObj;

		private Listener<String> getReqStringListener() {
			return new Response.Listener<String>() {
				public void onResponse(String response) {
					strObj = response;
				}
			};
		}

		private Response.Listener<JSONObject> getReqListener() {
			return new Response.Listener<JSONObject>() {
				public void onResponse(JSONObject response) {
					obj = response;
				}
			};
		}

		private Response.ErrorListener getReqErrorListener() {
			return new Response.ErrorListener() {
				public void onErrorResponse(VolleyError error) {
				}
			};
		}

		/**
		 * Writes a request for a connection
		 * 
		 * @param connection
		 * @param textBody
		 */
		private boolean writeRequest(HttpURLConnection connection,
				String textBody) {
			try {
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						connection.getOutputStream()));
				bw.write(textBody);
				bw.flush();
				bw.close();
				return true;
			} catch (IOException e) {
				return false;
			}
		}

		/**
		 * Parses the connection's response
		 * 
		 * @param connection
		 *            the connection
		 * @return
		 */
		private String readResponse(HttpURLConnection connection) {
			StringBuilder str = new StringBuilder();

			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				String line = "";
				while ((line = br.readLine()) != null) {
					str.append(line + "\n");
				}

				return str.toString();
			} catch (IOException e) {
				return new String();
			}
		}

		/**
		 * Encoding consumer key and consumer secret.
		 * 
		 * @param consumerKey
		 *            The consumer key found at
		 *            https://apps.twitter.com/app/5916351/keys
		 * @param consumerSecret
		 *            The consumer secret found at
		 *            https://apps.twitter.com/app/5916351/keys
		 * @return Keys encoded.
		 */
		public String encodeKeys(String consumerKey, String consumerSecret) {
			try {
				String encodedConsumerKey = URLEncoder.encode(consumerKey,
						"UTF-8");
				String encodedConsumerSecret = URLEncoder.encode(
						consumerSecret, "UTF-8");

				String fullKey = encodedConsumerKey + ":"
						+ encodedConsumerSecret;
				byte[] encodedBytes = Base64.encodeBase64(fullKey.getBytes());
				return new String(encodedBytes);
			} catch (UnsupportedEncodingException e) {
				return new String();
			}
		}
	}

	private class TweetListAdapter extends ArrayAdapter<Tweet> {
		private ArrayList<Tweet> adapterTweets;

		public TweetListAdapter(Context context, int textViewResourceId,
				ArrayList<Tweet> items) {
			super(context, textViewResourceId, items);
			this.adapterTweets = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.layout_tweet, null);
			}
			Tweet tweet = adapterTweets.get(position);
			TextView tText = (TextView) v.findViewById(R.id.toptext);
			TextView bText = (TextView) v.findViewById(R.id.bottomtext);
			tText.setText(tweet.content);
			bText.setText(tweet.author);

			return v;
		}

	}
}
