/**
 * Period.java
 * May 12, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

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

	public String getStartTimeAsString() {
		return parseTime(mStartTime);
	}

	public String getEndTimeAsString() {
		return parseTime(mEndTime);
	}

	private String parseTime(STime t) {
		int minutes = t.minute;// getMinutes();
		if (minutes < 10)
			return t.hour + ":0" + t.minute;
		else
			return t.hour + ":" + t.minute;
	}

	@Override
	public String toString() {
		String s = new String(mPeriodShort);
		s += " " + mClassName;
		s += ", " + getStartTimeAsString();
		s += "-" + getEndTimeAsString();
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
}
