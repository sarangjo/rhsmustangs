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
	public static final String BASE_DOMAIN = "https://832a98014b7c1301510632d504e2faa0bc9b2096.googledrive.com/host/0B9RPYw9KBJQEelJ1X0Q4b1dJVWs/";

	public static final String UPDATES_FILE = "schedule.txt";

	/**
	 * Downloads the full file.
	 * 
	 * @param url the URL of the file
	 * @return the full text of the file; returns "" if there was an error
	 */
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

	/**
	 * Downloads the base schedule for the given day.
	 * 
	 * @param day day of week; 1 = Monday, 5 = Friday
	 * @return the full base schedule
	 */
	public String getBaseDay(int day) {
		return getFullFile(BASE_DOMAIN + day + ".txt");
	}

	public String getMisc() {
		return getFullFile(BASE_DOMAIN + "misc.txt");
	}
}
