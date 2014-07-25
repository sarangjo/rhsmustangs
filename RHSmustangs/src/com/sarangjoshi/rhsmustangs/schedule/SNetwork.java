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
	public static final String BASE_DOMAIN = "https://e91cdbe99b3b9c74a0c3c3b13951890ecc0d6353.googledrive.com/host/0B9RPYw9KBJQEcTVub0w4WmlSN0U/";

	public static final String UPDATES_FILE = "schedule.txt";

	private String getFullFile(String url) {
		DefaultHttpClient hClient = new DefaultHttpClient();

		HttpGet hGet = new HttpGet(url);
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

	/**
	 * Downloads the latest update of the schedule updates file. If there is
	 * some error, returns "".
	 */
	public String getLatestUpdateTime() {
		DefaultHttpClient hClient = new DefaultHttpClient();

		HttpGet hGet = new HttpGet(BASE_DOMAIN + UPDATES_FILE);
		try {
			HttpResponse response = hClient.execute(hGet);
			HttpEntity ht = response.getEntity();

			BufferedHttpEntity buf = new BufferedHttpEntity(ht);
			InputStream is = buf.getContent();
			BufferedReader r = new BufferedReader(new InputStreamReader(is));

			return r.readLine().trim();
		} catch (IOException e) {
			return "";
		}
	}

	/**
	 * Downloads the schedule updates file. If there is some error, returns
	 * "NA".
	 */
	public String getUpdatesFileText() {
		String x = getFullFile(BASE_DOMAIN + UPDATES_FILE);
		return (x == "") ? "NA" : x;
	}

	public String getBaseDay(int day) {
		return getFullFile(BASE_DOMAIN + day + ".txt");
	}
}
