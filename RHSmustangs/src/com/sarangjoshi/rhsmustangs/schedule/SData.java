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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.format.Time;

public class SData {
	private SharedPreferences mPref;

	private Context mContext;

	private enum PrefType {
		DEFAULT, PERIODS, HOLIDAYS
	}

	// SharedPreference files
	private static final String PREF_NAME = "schedule_pref";
	private static final String PERIODS_PREF_NAME = "periods_pref";
	private static final String HOLIDAYS_PREF_NAME = "holidays_pref";
	// Internal Storage files
	private static final String UPDATES_NAME = "updates_file";
	private static final String BASE_NAME = "base_file_";
	private static final String UPDATES_GROUPNAMES_NAME = "update_groupnames_file";

	// SharedPreference keys
	private static final String LUNCH_KEY = "lunch";
	private static final String PERIOD_BASE_KEY = "period";
	private static final String UPDATETIME_KEY = "update";
	private static final String HOLIDAYSTIME_KEY = "holidays_time";
	private static final String NOTIF_KEY = "notif";
	private static final String INIT_KEY = "init";
	private static final String LATESTDAY_KEY = "saved_day";

	public SData(Context context) {
		mContext = context;
	}

	/**
	 * Initializes the SharedPreferences object.
	 */
	private void setupPref(PrefType pt) {
		if (mPref == null) {
			String s = "";
			switch (pt) {
			case DEFAULT:
				s = PREF_NAME;
				break;
			case PERIODS:
				s = PERIODS_PREF_NAME;
				break;
			case HOLIDAYS:
				s = HOLIDAYS_PREF_NAME;
				break;
			}
			mPref = mContext.getSharedPreferences(s, 0);
		}
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
		return mPref.getString(LUNCH_KEY, "a").charAt(0);
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

	public boolean setPeriodName(int i, String newName) {
		setupPref(PrefType.PERIODS);
		return mPref.edit().putString(PERIOD_BASE_KEY + i, newName).commit();
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
	 * Gets the period name from the shared preference for a <i>period
	 * number</i>.
	 * 
	 * @param i
	 *            period number
	 * @return saved period name
	 */
	public String getPeriodName(int i) {
		setupPref(PrefType.PERIODS);
		return mPref.getString(PERIOD_BASE_KEY + i, "");
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
	 * Deletes the custom name of a period, given the <i>period number<i>.
	 * 
	 * @param i
	 *            period number
	 * @return
	 */
	public boolean deletePeriodName(int i) {
		setupPref(PrefType.PERIODS);
		return mPref.edit().remove(PERIOD_BASE_KEY + i).commit();
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

	/**
	 * Deletes all the period names.
	 * 
	 * @return success
	 */
	public boolean deletePeriods() {
		setupPref(PrefType.PERIODS);
		return mPref.edit().clear().commit();
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
		String updatesFileText;
		// if (updatesFileText == null || updatesFileText == "") {
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
		// }

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

	/**
	 * Deletes the saved updates.
	 * 
	 * @return the success of the delete
	 */
	public boolean deleteSavedUpdates() {
		File dir = mContext.getFilesDir();
		File file = new File(dir, UPDATES_NAME);
		boolean a = file.delete();

		setupPref(PrefType.DEFAULT);
		boolean b = mPref.edit().remove(UPDATETIME_KEY).commit();

		return a && b;
	}

	// NOTIFICATION
	/**
	 * Saves if the notification has been created or not.
	 * 
	 * @return if the save was successful
	 */
	public boolean saveNotification(boolean created) {
		setupPref(PrefType.DEFAULT);
		return mPref.edit().putBoolean(NOTIF_KEY, created).commit();
	}

	/**
	 * Checks whether the notification has been created or not.
	 */
	public boolean getIsNotifCreated() {
		setupPref(PrefType.DEFAULT);
		return mPref.getBoolean(NOTIF_KEY, false);
	}

	// BASE SCHEDULE
	/**
	 * Saves the base schedule.
	 * 
	 * @param day
	 *            the day index to save; 1 = Monday, 5 = Friday
	 * @param schedule
	 *            the schedule
	 * @return success
	 */
	public boolean saveBaseDay(int day, String schedule) {
		try {
			FileOutputStream fos = mContext.openFileOutput(BASE_NAME + day,
					Context.MODE_PRIVATE);
			fos.write(schedule.getBytes());
			fos.close();
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	/**
	 * Gets the base schedule.
	 * 
	 * @param day
	 *            the day index; 1 = Monday, 5 = Friday
	 */
	public String getBaseSchedule(int day) {
		StringBuffer datax = new StringBuffer("");
		try {
			FileInputStream fis = mContext.openFileInput(BASE_NAME + day);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);

			String readString = br.readLine();
			while (readString != null) {
				datax.append(readString + "\n");
				readString = br.readLine();
			}

			isr.close();

			return datax.toString();
		} catch (IOException e) {
			return "";
		}
	}

	/**
	 * Deletes ALL the base schedules saved.
	 * 
	 * @return success
	 */
	public boolean deleteBase() {
		File dir = mContext.getFilesDir();
		boolean a = true;
		for (int i = 1; i <= 5; i++) {
			File file = new File(dir, BASE_NAME + i);
			a = a && file.delete();
		}

		return a;
	}

	// INITIALIZATION
	/**
	 * Saves whether the schedule has been initialized.
	 * 
	 * @return success
	 */
	public boolean saveInitialize(boolean initialized) {
		setupPref(PrefType.DEFAULT);
		return mPref.edit().putBoolean(INIT_KEY, initialized).commit();
	}

	/**
	 * Gets whether the schedule has been initialized or not.
	 */
	public boolean getIsInitialized() {
		setupPref(PrefType.DEFAULT);
		return mPref.getBoolean(INIT_KEY, false);
	}

	// MISC DETAILS
	/**
	 * Saves the given miscellaneous detail.
	 * 
	 * @param key
	 *            the misc detail key
	 * @param value
	 *            the misc detail value
	 * @return success
	 */
	public boolean saveMiscDetail(String key, int value) {
		setupPref(PrefType.DEFAULT);
		return mPref.edit().putInt(key, value).commit();
	}

	/**
	 * Gets the miscellaneous detail.
	 * 
	 * @param val
	 *            the misc detail key
	 * @return success
	 */
	public int getMiscDetail(String val) {
		setupPref(PrefType.DEFAULT);
		return mPref.getInt(val, 0);
	}

	// PERIOD GROUP NAMES
	public boolean savePeriodGroups(String s) {
		try {
			// FileOutputStream fos =
			// mContext.openFileOutput(UPDATES_GROUPNAMES_NAME,
			// Context.MODE_APPEND);
			FileOutputStream fos = mContext.openFileOutput(
					UPDATES_GROUPNAMES_NAME, Context.MODE_PRIVATE);
			fos.write(s.getBytes());
			fos.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public String[] getPeriodGroups(String day) {
		ArrayList<String> allNames = new ArrayList<String>();

		try {
			FileInputStream fis = mContext
					.openFileInput(UPDATES_GROUPNAMES_NAME);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);

			String readString = "";
			while ((readString = br.readLine()) != null) {
				if (readString.substring(0, readString.indexOf(" "))
						.equals(day))
					allNames.add(readString);
			}
		} catch (IOException e) {

		}
		String[] allNamesArr = new String[allNames.size()];
		allNames.toArray(allNamesArr);
		return allNamesArr;
	}

	public boolean clearPeriodGroupNames() {
		return mContext.deleteFile(UPDATES_GROUPNAMES_NAME);
	}

	public boolean saveGroupPref(String day, int n) {
		setupPref(PrefType.DEFAULT);
		if (day.length() > 8)
			day = day.substring(0, 8);
		return mPref.edit().putInt(day + "_GRP", n).commit();
	}

	public int getGroupPref(String day) {
		setupPref(PrefType.DEFAULT);
		if (day.length() > 8)
			day = day.substring(0, 8);
		return mPref.getInt(day + "_GRP", 1);
	}

	// LAST DAY
	public boolean saveLatestDay(String lDay) {
		setupPref(PrefType.DEFAULT);
		return mPref.edit().putString(LATESTDAY_KEY, lDay).commit();
	}

	public String getLatestDay() {
		setupPref(PrefType.DEFAULT);
		return mPref.getString(LATESTDAY_KEY, "");
	}

	// HOLIDAYS
	public boolean saveHoliday(Time startT, String name) {
		setupPref(PrefType.HOLIDAYS);
		return mPref.edit().putString(startT.toString().substring(0, 8), name)
				.commit();
	}

	public String getHolidayName(String date) {
		setupPref(PrefType.HOLIDAYS);
		return mPref.getString(date, null);
	}

	public Map<String, ?> getAllHolidays() {
		setupPref(PrefType.HOLIDAYS);
		return mPref.getAll();
	}

	public boolean saveHolidaysUpdateTime(String updateTime) {
		setupPref(PrefType.DEFAULT);
		return mPref.edit().putString(HOLIDAYSTIME_KEY, updateTime).commit();
	}

	public String getHolidaysUpdateTime() {
		setupPref(PrefType.DEFAULT);
		return mPref.getString(HOLIDAYSTIME_KEY, "");
	}

	public boolean deleteAllHolidays() {
		setupPref(PrefType.HOLIDAYS);
		return mPref.edit().clear().commit();
	}
}
