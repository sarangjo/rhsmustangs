/**
 * Period.java
 * May 12, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

public class Period {
	public String mPeriodNum = "1";
	public String mClassName = "Period";
	public ScheduleTime mStartTime;
	public ScheduleTime mEndTime;

	public Period(String periodNum, String periodName, int sh, int sm, 
			int eh, int em) {
		mPeriodNum = periodNum;
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
		String s = new String(mPeriodNum);
		s += " " + mClassName;
		s += ", " + getStartTimeAsString();
		s += "-" + getEndTimeAsString();
		return s;
	}
}
