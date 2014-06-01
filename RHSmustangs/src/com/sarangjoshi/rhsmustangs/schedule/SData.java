/**
 * ScheduleData.java
 * May 14, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import com.sarangjoshi.rhsmustangs.schedule.Period.PeriodStyle;

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
	 * @param p the given period
	 * @return the SharedPreferences key
	 */
	public String getKey(Period p) {
		// The KEY is dependent on whether it's a class or not.
		if(p.getPeriodStyle() == PeriodStyle.CLASS)
			return PERIOD_BASE_KEY + p.getPeriodNumber();
		else
			return PERIOD_BASE_KEY + p.mPeriodShort;
		
	}
}
