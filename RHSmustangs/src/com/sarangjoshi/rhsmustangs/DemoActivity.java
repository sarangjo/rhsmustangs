package com.sarangjoshi.rhsmustangs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONValue;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sarangjoshi.rhsmustangs.R;

import android.app.*;
import android.content.Context;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;

public class DemoActivity extends Activity {
	TextView resultView;
	String results;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);

		resultView = (TextView) this.findViewById(R.id.textview1);

		new JSONRequestTask().execute();
		
		
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
			progressDialog = ProgressDialog.show(DemoActivity.this, "",
					"Loading...", true);

			queue = Volley.newRequestQueue(DemoActivity.this);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				String endUrl = "https://dl.dropboxusercontent.com/u/57707756/CapTech_Volley_Blog/stringResponse.json";

				appOnlyAuth(endUrl);
			} catch (Exception e) {
				Log.e("TwitterActivity", "Error loading JSON", e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();

			if (results != null)
				resultView.setText(results);
		}

		public void appOnlyAuth(String endpointUrl) throws IOException {

			getRequestBearerTokenAlt(endpointUrl);

		}

		public void getRequestBearerTokenAlt(String endUrl) throws IOException {
			StringRequest postRequest = new StringRequest(Request.Method.POST,
					endUrl, new ResponseListener(), new ResponseErrorListener());

			queue.add(postRequest);

			if (queue.getCache().get(endUrl) != null) {
				String cachedResponse = new String(
						queue.getCache().get(endUrl).data);
				System.out.println(cachedResponse);
			}
		}

		public String bearerToken;

		private class ResponseListener implements Response.Listener<String> {
			public void onResponse(String response) {
				String s = response;
			}
		}

		private class ResponseErrorListener implements Response.ErrorListener {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}
	}
}
