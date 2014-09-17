/**
 * Period.java
 * May 12, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import android.text.format.Time;

public class SPeriod {
	public String mPeriodShort = "0";
	public String mClassName = "Period";
	public boolean isCustomizable = true;
	public STime mStartTime = new STime(6, 30);
	public STime mEndTime = new STime(7, 24);
	public int groupN = '0';

	public static enum PeriodStyle {
		HOMEROOM, LUNCH, CLASS, OTHER
	}

	public SPeriod(String periodShort, String periodName, int sh, int sm,
			int eh, int em, int gn) {
		mPeriodShort = periodShort;
		mClassName = periodName;
		mStartTime = new STime(sh, sm);
		mEndTime = new STime(eh, em);
		groupN = gn;
	}

	public SPeriod() {
	}

	public String getStartTimeAsString(boolean is24hr) {
		return parseTime(mStartTime, is24hr);
	}

	public String getEndTimeAsString(boolean is24hr) {
		return parseTime(mEndTime, is24hr);
	}

	private String parseTime(STime t, boolean is24hr) {
		String h = "", p = (t.hour >= 12) ? "pm" : "am";
		if (is24hr) {
			h += t.hour;
		} else {
			if (t.hour == 0)
				h += 12;
			else if (t.hour > 12) {
				h += (t.hour % 12);
			} else
				h += t.hour;
		}
		String m = "";
		m += (t.minute < 10) ? ("0" + t.minute) : t.minute;

		return h + ":" + m + p;
	}

	@Override
	public String toString() {
		String s = new String(mPeriodShort);
		s += " " + mClassName;
		s += ", " + getStartTimeAsString(true);
		s += "-" + getEndTimeAsString(true);
		return s;
	}

	public PeriodStyle getPeriodStyle() {
		if (mPeriodShort.charAt(0) == 'L')
			return PeriodStyle.LUNCH;
		else if (mPeriodShort.equals("HR"))
			return PeriodStyle.HOMEROOM;
		else
			return PeriodStyle.CLASS;
	}

	/**
	 * Gets the default period name to display regardless of user settings.
	 * 
	 * @param p
	 *            the period
	 * @return the period name
	 */
	public String getDefaultPeriodName() {
		switch (getPeriodStyle()) {
		case CLASS:
			return "Period " + mPeriodShort;
		case HOMEROOM:
			return "Homeroom";
		case LUNCH:
			return "Lunch";
		default:
			return mClassName;
		}
	}

	public static SPeriod holiday(String holName) {
		return new SPeriod("HD", holName, 0, 0, 23, 59, 0);
	}

	public static class STime {
		public int hour;
		public int minute;

		public STime(int nH, int nM) {
			hour = nH % 24;
			if (minute >= 60) {
				hour += (int) (minute / 60);
				minute = nM % 60;
			} else if (minute < 0) {
				hour += (int) (minute / 60);
				minute = nM % 60;
			} else
				minute = nM;
		}

		public STime(Time time) {
			this(time.hour, time.minute);
		}

		public boolean isBefore(STime t) {
			if (hour < t.hour)
				return true;
			else if (hour == t.hour && minute < t.minute)
				return true;
			else
				return false;
		}

		public boolean isAfter(STime t) {
			if (hour > t.hour)
				return true;
			else if (hour == t.hour && minute > t.minute)
				return true;
			else
				return false;
		}
	}

}
