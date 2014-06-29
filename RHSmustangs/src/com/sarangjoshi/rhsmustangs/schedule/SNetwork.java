/**
 * SNetwork.java
 * Jun 15, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class SNetwork {
	private Context mContext;

	private String fileT = "https://googledrive.com/host/0B9RPYw9KBJQEcTVub0w4WmlSN0U/test.txt";

	public SNetwork(Context context) {
		mContext = context;
	}

	public String getUpdatesFileText() {/*
		try {
			return new GetTextTask().execute().get();
		} catch (Exception e) {
			return "";
		}
	}

	private class GetTextTask extends AsyncTask<Void, Void, String> {
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(mContext, "", "Downloading schedule...");
		}
		
		@Override
		protected String doInBackground(Void... args) {*/
			DefaultHttpClient hClient = new DefaultHttpClient();

			HttpGet hGet = new HttpGet(fileT);
			try {
				HttpResponse response = hClient.execute(hGet);
				HttpEntity ht = response.getEntity();

				BufferedHttpEntity buf = new BufferedHttpEntity(ht);

				InputStream is = buf.getContent();

				BufferedReader r = new BufferedReader(new InputStreamReader(is));

				StringBuilder total = new StringBuilder();

				String line;
				while ((line = r.readLine()) != null) {
					total.append(line + "\n");
				}
				return total.toString();
			} catch (IOException e) {
				return "";
			}/*
		}
		
		@Override
		protected void onPostExecute(String s) {
			pd.dismiss();
		}*/
	}
}
