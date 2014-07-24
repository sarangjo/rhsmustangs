/**
 * ScheduleData.java
 * May 14, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SData {
	private SharedPreferences mPref;

	private Context mContext;

	private String updatesFileText;

	private enum PrefType {
		DEFAULT, PERIODS
	}

	private static final String PREF_NAME = "schedule_pref";
	private static final String PERIODS_PREF_NAME = "periods_pref";
	private static final String UPDATES_NAME = "updates_file";

	// SharedPreference keys
	private static final String LUNCH_KEY = "lunch";
	private static final String PERIOD_BASE_KEY = "period";
	private static final String UPDATETIME_KEY = "update";

	public SData(Context context) {
		mContext = context;
	}

	/**
	 * Initializes the SharedPreferences object.
	 */
	private void setupPref(PrefType pt) {
		if (mPref == null)
			mPref = mContext
					.getSharedPreferences((pt == PrefType.DEFAULT) ? PREF_NAME
							: PERIODS_PREF_NAME, 0);
	}

	// LUNCH
	/**
	 * Saves the new lunch to the shared preference.
	 * 
	 * @param lunch
	 *            the new lunch
	 */
	public void setLunch(char lunch) {
		setupPref(PrefType.DEFAULT);
		Editor e = mPref.edit();
		e.putString(LUNCH_KEY, lunch + "");
		e.commit();
	}

	/**
	 * Gets the lunch from the shared preference.
	 * 
	 * @return the lunch char
	 */
	public char getLunch() {
		setupPref(PrefType.DEFAULT);
		return mPref.getString(LUNCH_KEY, "c").charAt(0);
	}

	// PERIOD NAMES
	/**
	 * Saves the new period name to the shared preference.
	 * 
	 * @param p
	 *            the period which corresponds to the SharedPref key
	 * @param newName
	 *            the new period name
	 */
	public boolean setPeriodName(Period p, String newName) {
		setupPref(PrefType.PERIODS);
		return mPref.edit().putString(getKey(p), newName).commit();
	}

	/**
	 * Gets the period name from the shared preference. If a custom period name
	 * is saved, this will be returned. Otherwise, the default will be returned. <br>
	 * Ex.: "Period 1", "Homeroom", "Lunch"
	 * 
	 * @param p
	 *            the period
	 * @return the period name corresponding to that period
	 */
	public String getPeriodName(Period p) {
		setupPref(PrefType.PERIODS);
		String defaultName = p.getDefaultPeriodName();
		return mPref.getString(getKey(p), defaultName);
	}

	/**
	 * Deletes the custom name of the given period.
	 * 
	 * @param p
	 */
	public boolean deletePeriodName(Period p) {
		setupPref(PrefType.PERIODS);
		return mPref.edit().remove(getKey(p)).commit();
	}

	/**
	 * Deletes all custom Period names.
	 */
	public boolean resetPeriods() {
		setupPref(PrefType.PERIODS);
		return mPref.edit().clear().commit();
	}

	/**
	 * Gets the SharedPreferences key for the given Period.
	 * 
	 * @param p
	 *            the given period
	 * @return the SharedPreferences key
	 */
	public String getKey(Period p) {
		return PERIOD_BASE_KEY + p.mPeriodShort;
	}

	// SCHEDULE UPDATES
	/**
	 * Saves the given string to the updates file. Also updates the update time
	 * in the preferences.
	 * 
	 * @param updates
	 *            the updates file string
	 * @return
	 * @throws IOException
	 */
	public boolean saveUpdates(String updates) {
		updatesFileText = updates;

		// Saves update time as well
		try {
			String updateTime = updates.substring(0, updates.indexOf('\n'));
			saveUpdateTime(updateTime);
		} catch (IndexOutOfBoundsException e) {

		}

		try {
			FileOutputStream fos = mContext.openFileOutput(UPDATES_NAME,
					Context.MODE_PRIVATE);
			fos.write(updates.getBytes());
			fos.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * If the local variable is null, pulls the String from the file.
	 * 
	 * @return the updates string
	 */
	public String getUpdatesString() throws IOException {
		if (updatesFileText == null || updatesFileText == "") {
			StringBuffer datax = new StringBuffer("");

			try {
				FileInputStream fis = mContext.openFileInput(UPDATES_NAME);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader buffreader = new BufferedReader(isr);

				String readString = buffreader.readLine();
				while (readString != null) {
					datax.append(readString + "\n");
					readString = buffreader.readLine();
				}

				isr.close();

				updatesFileText = datax.toString();
			} catch (FileNotFoundException e) {
				updatesFileText = "";
			}
		}

		return updatesFileText;
	}

	/**
	 * Saves the update time in RFC 2445 format.
	 */
	public boolean saveUpdateTime(String updateTime) {
		setupPref(PrefType.DEFAULT);
		return mPref.edit().putString(UPDATETIME_KEY, updateTime).commit();
	}

	/**
	 * Retrieves the latest time of update of the schedule.
	 */
	public String getUpdateTime() {
		setupPref(PrefType.DEFAULT);
		return mPref.getString(UPDATETIME_KEY, "");
	}

	public boolean deleteSavedUpdates() {
		File dir = mContext.getFilesDir();
		File file = new File(dir, UPDATES_NAME);
		updatesFileText = "";
		boolean a = file.delete();

		setupPref(PrefType.DEFAULT);
		boolean b = mPref.edit().remove(UPDATETIME_KEY).commit();

		return a && b;
	}
}
