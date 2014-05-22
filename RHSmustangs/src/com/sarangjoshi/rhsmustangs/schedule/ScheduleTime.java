/**
 * ScheduleTime.java
 * May 20, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import android.text.format.Time;

public class ScheduleTime {
	public int hour;
	public int minute;

	public ScheduleTime(int nH, int nM) {
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

	public ScheduleTime(Time time) {
		this(time.hour, time.minute);
	}

	public boolean isBefore(ScheduleTime t) {
		if (hour < t.hour)
			return true;
		else if (hour == t.hour && minute < t.minute)
			return true;
		else
			return false;
	}

	public boolean isAfter(ScheduleTime t) {
		if (hour > t.hour)
			return true;
		else if (hour == t.hour && minute > t.minute)
			return true;
		else
			return false;
	}
}
