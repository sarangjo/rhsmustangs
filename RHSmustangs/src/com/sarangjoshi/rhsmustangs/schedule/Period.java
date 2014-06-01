/**
 * Period.java
 * May 12, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

public class Period {
	public String mPeriodShort = "P0";
	public String mClassName = "Period";
	public ScheduleTime mStartTime;
	public ScheduleTime mEndTime;

	public static enum PeriodStyle {
		HOMEROOM, LUNCH, CLASS
	}

	public Period(String periodNum, String periodName, int sh, int sm, int eh,
			int em) {
		mPeriodShort = periodNum;
		mClassName = periodName;
		mStartTime = new ScheduleTime(sh, sm);
		mEndTime = new ScheduleTime(eh, em);
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

	public int getPeriodNumber() {
		int periodN = -1;

		if (getPeriodStyle() == PeriodStyle.CLASS)
			periodN = Integer.parseInt(""
					+ ((mPeriodShort.charAt(0) == 'P') ? mPeriodShort.charAt(1)
							: mPeriodShort.charAt(0)));

		return periodN;
	}

	/**
	 * Gets the default period name to display regardless of user settings.
	 * 
	 * @param p the period 
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
