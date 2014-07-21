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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class SNetwork {
	public static final String fileT = "https://e91cdbe99b3b9c74a0c3c3b13951890ecc0d6353.googledrive.com/host/0B9RPYw9KBJQEcTVub0w4WmlSN0U/schedule.txt";

	// "https://raw.githubusercontent.com/FinalThunder526/rhsmustangs/master/files/schedule.txt";

	public String getLatestUpdate() {
		DefaultHttpClient hClient = new DefaultHttpClient();

		HttpGet hGet = new HttpGet(fileT);
		try {
			HttpResponse response = hClient.execute(hGet);
			HttpEntity ht = response.getEntity();

			BufferedHttpEntity buf = new BufferedHttpEntity(ht);
			InputStream is = buf.getContent();
			BufferedReader r = new BufferedReader(new InputStreamReader(is));

			return r.readLine();
		} catch (IOException e) {
			return "";
		}
	}

	public String getUpdatesFileText() {
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
		}
	}
}
