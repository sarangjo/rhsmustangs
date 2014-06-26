/**
 * Period.java
 * May 12, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

public class Period {
	public String mPeriodShort = "0";
	public String mClassName = "Period";
	public boolean isCustomizable = true;
	public ScheduleTime mStartTime = new ScheduleTime(6, 30);
	public ScheduleTime mEndTime = new ScheduleTime(7, 24);
	public char lunchStyle = '0';

	public static enum PeriodStyle {
		HOMEROOM, LUNCH, CLASS
	}

	public Period(String periodNum, String periodName, int sh, int sm, int eh,
			int em, char ls) {
		mPeriodShort = periodNum;
		mClassName = periodName;
		mStartTime = new ScheduleTime(sh, sm);
		mEndTime = new ScheduleTime(eh, em);
		lunchStyle = ls;
	}

	public Period() {
	}

	public String getStartTimeAsString() {
		return parseTime(mStartTime);
	}

	public String getEndTimeAsString() {
		return parseTime(mEndTime);
	}

	private String parseTime(ScheduleTime t) {
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

	public String getPeriodNumber() {
		if (getPeriodStyle() == PeriodStyle.CLASS) {
			// If the first character is a number, "3" "4B" "6" then the period
			// number is the first character.
			try {
				return "" + Integer.parseInt(mPeriodShort.charAt(0) + "");
			} catch (NumberFormatException e) {
				// In this case the first character is not a number, so just
				// returns the whole damn mPeriodShort.
				return mPeriodShort;
			}
		}

		return "-1";
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
			return "Period " + getPeriodNumber();
		case HOMEROOM:
			return "Homeroom";
		case LUNCH:
			return "Lunch";
		}
		return "";
	}
}
