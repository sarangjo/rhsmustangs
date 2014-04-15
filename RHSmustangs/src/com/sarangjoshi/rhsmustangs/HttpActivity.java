package com.sarangjoshi.rhsmustangs;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.sarangjoshi.rhsmustangs.WebParser.Parse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class HttpActivity extends Activity {
	WebParser parse;
	TextView view1;
	String myUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(textView);
		setContentView(R.layout.activity_http);

		parse = new WebParser();

		// Get data from intent
		Intent intent = getIntent();
		myUrl = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

		// TextView textView = new TextView(this);

		view1 = (TextView) this.findViewById(R.id.textview1);// (R.id.textview1);

		loadHttpData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void loadHttpData() {
		// Gets the system data on the network status
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// netInf stores all the current network status information
		NetworkInfo netInf = connMgr.getActiveNetworkInfo();

		if (netInf != null && netInf.isConnected()) {
			// Network is connected
			if (myUrl != null)
				new GetHeadingTask().execute(myUrl);
		} else {
			// Network is not connected
			view1.setText("Unable to access network.");
		}
	}

	/**
	 * This is the core of network tasks in Android. An AsyncTask is basically
	 * an asynchronous task that runs in parallel with the main UI thread, so
	 * that instead of having the UI being held up for network waits, that
	 * happens in a separate thread.
	 * 
	 * This particular method retrieves the given URL and sets the text of
	 * {@link view1} to the first 500 characters of the web page.
	 * 
	 * @author Sarang
	 * 
	 */
	private class GetHeadingTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String[] urls) {
			String myURL = urls[0];
			//myURL = "http://m.facebook.com/RedmondHighASB?v=timeline";
			myURL = "https://mobile.twitter.com/redmondasb";
			try {
				return parse.parsePage(myURL, Parse.TWITTER);
				//return downloadURL(myURL);
			} catch (IOException e) {
				return "Unable to get webpage. Check URL.";
			}
		}

		@Override
		protected void onPostExecute(String heading) {
			view1.setText(heading);
		}
	}

	/**
	 * Downloads the URL.
	 * 
	 * @param myUrl
	 *            the url
	 * @return the first 500 characters of the webpage
	 * @throws IOException
	 *             in case there are errors in IO
	 */
	private String downloadURL(String myUrl) throws IOException {
		InputStream input = null;

		// length of retrieved webpage
		int len = 2500;

		try {
			URL url = new URL(myUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url
					.openConnection();
			httpConn.setReadTimeout(10000);
			httpConn.setConnectTimeout(15000);
			httpConn.setRequestMethod("GET");
			httpConn.setDoInput(true);

			// Start query
			httpConn.connect();
			int responseCode = httpConn.getResponseCode();
			Log.d("Http_example", "The response code is " + responseCode);
			input = httpConn.getInputStream();

			String contentAsString = readInputLen(input, len);
			return contentAsString;
		} finally {
			if (input != null)
				input.close();
		}
	}

	private String readInput(InputStream input) throws IOException,
			UnsupportedEncodingException {
		Reader reader = new InputStreamReader(input, "UTF-8");
		int isEnd = 0;
		String s = "";
		char[] buffer = new char[1];
		while(isEnd != -1) {
			isEnd = reader.read(buffer);
			s += new String(buffer);
		}
		return s;
	}

	private String readInputLen(InputStream input, int len) throws IOException,
			UnsupportedEncodingException {
		Reader reader = null;
		reader = new InputStreamReader(input, "UTF-8");
		char[] buffer = new char[len];
		reader.read(buffer);
		return new String(buffer);
	}
}
