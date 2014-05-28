/**
 * ScheduleData.java
 * May 14, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SData {
	private SharedPreferences mPref;

	private char mLunch = 'c';
	private Context mContext;
	private static final String PREF_NAME = "schedule_pref";
	private static final String LUNCH_KEY = "lunch";
	private static final String PERIOD_BASE_KEY = "period";

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
	 * @return
	 */
	public char getLunch() {
		setupPref();
		mLunch = mPref.getString(LUNCH_KEY, "c").charAt(0);
		return mLunch;
	}

	/**
	 * Saves the new period name to the shared preference.
	 * 
	 * @param periodNum
	 *            the period number tag which corresponds to the SharedPref key
	 * @param newName
	 *            the new period name
	 */
	public void setPeriodName(String periodNum, String newName) {
		setupPref();
		Editor e = mPref.edit();
		e.putString(PERIOD_BASE_KEY + periodNum, newName);
		e.commit();
	}

	/**
	 * Gets the period name from the shared preference. If a custom period name
	 * is saved, this will be returned. Otherwise, the default will be returned.
	 * <br> 
	 * Ex.: "Period 1", "Homeroom", "Lunch"
	 * 
	 * @param p
	 *            the period
	 * @return the period name corresponding to that period
	 */
	public String getPeriodName(Period p) {
		setupPref();
		String defaultName = getDefaultPeriodName(p);
		// The KEY has the periodNum, but the actual VALUE returned involves
		// getPeriodNumber()
		return mPref.getString(PERIOD_BASE_KEY + p.mPeriodNum, defaultName);
	}
	
	/**
	 * Gets the default period name regardless of user settings.
	 * 
	 * @param p the period 
	 * @return the period name
	 */
	public String getDefaultPeriodName(Period p) {
		switch (p.getPeriodStyle()) {
		case CLASS:
			return "Period " + p.getPeriodNumber();
		case HOMEROOM:
			return "Homeroom";
		case LUNCH:
			return "Lunch";
		}
		return "";
	}

	public void deletePeriodName(String periodNum) {
		Editor e = mPref.edit();
		e.remove(PERIOD_BASE_KEY + periodNum);
		e.commit();
	}
}
