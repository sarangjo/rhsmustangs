/**
 * Period.java
 * May 12, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import java.sql.Time;

public class Period {
	public String mPeriodNum = "1";
	public String mClassName = "COMP SCI YO";
	public Time mStartTime;
	public Time mEndTime;

	public Period(String periodNum, String periodName, Time startTime,
			Time endTime) {
		mPeriodNum = periodNum;
		mClassName = periodName;
		mStartTime = startTime;
		mEndTime = endTime;
	}

	public Period() {
		// TODO Auto-generated constructor stub
	}

	public String getStartTime() {
		return parseTime(mStartTime);
	}

	public String getEndTime() {
		return parseTime(mEndTime);
	}

	private String parseTime(Time t) {
		int minutes = t.getMinutes();
		if (minutes < 10)
			return t.getHours() + ":0" + t.getMinutes();
		else
			return t.getHours() + ":" + t.getMinutes();
	}

	@Override
	public String toString() {
		String s = new String(mPeriodNum);
		s += " " + mClassName;
		s += ", " + getStartTime();
		s += "-" + getEndTime();
		return s;
	}
}
