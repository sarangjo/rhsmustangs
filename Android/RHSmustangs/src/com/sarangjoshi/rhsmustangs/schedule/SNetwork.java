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
	public static final String FILE_DOMAIN = "https://832a98014b7c1301510632d504e2faa0bc9b2096.googledrive.com/host/0B9RPYw9KBJQEelJ1X0Q4b1dJVWs/";

	public static final String UPDATES_FILE = "schedule.txt";
	public static final String HOLIDAYS_FILE = "holidays.txt";

	private BufferedReader getFileReader(String url) {
		DefaultHttpClient hClient = new DefaultHttpClient();

		HttpGet hGet = new HttpGet(url);
		try {
			HttpResponse response = hClient.execute(hGet);
			HttpEntity ht = response.getEntity();

			BufferedHttpEntity buf = new BufferedHttpEntity(ht);
			InputStream is = buf.getContent();
			return new BufferedReader(new InputStreamReader(is));
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Downloads the full file.
	 * 
	 * @param url
	 *            the URL of the file
	 * @return the full text of the file; returns "" if there was an error
	 */
	private String getFullFile(String url) {
		BufferedReader r = getFileReader(url);
		StringBuilder total = new StringBuilder();

		String line;
		try {
			while ((line = r.readLine()) != null)
				total.append(line + "\n");
		} catch (Exception e) {
			return "";
		}
		return total.toString();
	}

	private String getFirstLine(String url) {
		BufferedReader r = getFileReader(url);
		try {
			return r.readLine().trim();
		} catch (Exception e) {
			return "N/A";
		}
	}

	/**
	 * Downloads the latest update of the schedule updates file. If there is
	 * some error, returns "".
	 */
	public String getLatestUpdateTime() {
		return getFirstLine(FILE_DOMAIN + UPDATES_FILE);
	}

	/**
	 * Downloads the schedule updates file. If there is some error, returns
	 * "NA".
	 */
	public String getUpdatesFileText() {
		String x = getFullFile(FILE_DOMAIN + UPDATES_FILE);
		return (x == "") ? "N/A" : x;
	}

	public String getHolidaysUpdateTime() {
		return getFirstLine(FILE_DOMAIN + HOLIDAYS_FILE);
	}

	public String getHolidaysFileText() {
		return getFullFile(FILE_DOMAIN + HOLIDAYS_FILE);
	}

	/**
	 * Downloads the base schedule for the given day.
	 * 
	 * @param day
	 *            day of week; 1 = Monday, 5 = Friday
	 * @return the full base schedule
	 */
	public String getBaseDay(int day) {
		return getFullFile(FILE_DOMAIN + day + ".txt");
	}

	public String getBaseDetails() {
		return getFullFile(FILE_DOMAIN + "base.txt");
	}
	
	public String getMisc() {
		return getFullFile(FILE_DOMAIN + "misc.txt");
	}
}
