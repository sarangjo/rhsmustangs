/**
 * ScheduleData.java
 * May 14, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SData {
	private SharedPreferences mPref;

	private char mLunch = 'c';
	private Context mContext;

	private String updatesFileText;

	private static final String PREF_NAME = "schedule_pref";
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
	private void setupPref() {
		if (mPref == null)
			mPref = mContext.getSharedPreferences(PREF_NAME, 0);
	}

	/**
	 * Saves the new lunch to the shared preference.
	 * 
	 * @param lunch
	 *            the new lunch
	 */
	public void setLunch(char lunch) {
		setupPref();

		Editor e = mPref.edit();
		e.putString(LUNCH_KEY, lunch + "");
		e.commit();

		mLunch = lunch;
	}

	/**
	 * Gets the lunch from the shared preference.
	 * 
	 * @return the lunch char
	 */
	public char getLunch() {
		setupPref();
		mLunch = mPref.getString(LUNCH_KEY, "c").charAt(0);
		return mLunch;
	}

	/**
	 * Saves the new period name to the shared preference.
	 * 
	 * @param p
	 *            the period which corresponds to the SharedPref key
	 * @param newName
	 *            the new period name
	 */
	public void setPeriodName(Period p, String newName) {
		setupPref();
		Editor e = mPref.edit();
		e.putString(getKey(p), newName);
		e.commit();
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
		setupPref();
		String defaultName = p.getDefaultPeriodName();
		return mPref.getString(getKey(p), defaultName);
	}

	/**
	 * Deletes the custom name of the given period.
	 * 
	 * @param p
	 */
	public void deletePeriodName(Period p) {
		Editor e = mPref.edit();
		e.remove(getKey(p));
		e.commit();
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

	/**
	 * Saves the given string to the updates file.
	 * 
	 * @param updates
	 *            the updates file string
	 * @return
	 * @throws IOException
	 */
	public boolean saveUpdates(String updates) {
		updatesFileText = updates;

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
		if (updatesFileText == null) {
			StringBuffer datax = new StringBuffer("");

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
		}

		return updatesFileText;
	}

	public void setUpdateTime(String updateTime) {
		setupPref();
		Editor e = mPref.edit();
		e.putString(UPDATETIME_KEY, updateTime);
		e.commit();
	}

	public String getUpdateTime() {
		setupPref();
		return mPref.getString(UPDATETIME_KEY, "");
	}
}
