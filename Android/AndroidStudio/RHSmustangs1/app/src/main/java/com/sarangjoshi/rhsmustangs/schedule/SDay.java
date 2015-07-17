/**
 * SDay.java
 * Sep 29, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import java.util.ArrayList;

import android.text.format.Time;

public class SDay {
	private ArrayList<SPeriod> mPeriods;
	private Time mDate;
	private DayStyle mDStyle;

	public enum DayStyle {
		REGULAR, UPDATED, HOLIDAY
	}

	public SDay(Time newDate, DayStyle d) {
		mDate = newDate;
		mDStyle = d;
	}

	public String getDate() {
		return mDate.toString().substring(0, 8);
	}

	public ArrayList<SPeriod> getPeriods() {
		return mPeriods;
	}

	public void setPeriods(ArrayList<SPeriod> periods) {
		mPeriods = periods;
	}
}
