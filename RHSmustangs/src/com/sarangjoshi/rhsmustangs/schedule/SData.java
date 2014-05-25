/**
 * ScheduleData.java
 * May 14, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.format.Time;

public class SData {
	private SharedPreferences mPref;
	
	private char mLunch = 'c';
	private Context mContext;
	private final String PREF_NAME = "schedule_pref";
	private final String LUNCH_KEY = "lunch";
	
	public SData(Context context) {
		mContext = context;
		mPref = mContext.getSharedPreferences(PREF_NAME, 0);
	}

	public void setLunch(char lunch) {
		setupPref();
		
		Editor e = mPref.edit();
		e.putString(LUNCH_KEY, lunch + "");
		e.commit();
		
		mLunch = lunch;
	}
	
	private void setupPref() {
		if(mPref == null)
			mPref = mContext.getSharedPreferences(PREF_NAME, 0);		
	}

	public char getLunch() {
		setupPref();
		mLunch = mPref.getString(LUNCH_KEY, "c").charAt(0);
		return mLunch;
	}
}
