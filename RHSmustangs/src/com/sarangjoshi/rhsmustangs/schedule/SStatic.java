/**
 * ScheduleStaticData.java
 * May 23, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import java.util.ArrayList;

import android.text.format.Time;

public class SStatic {
	public static int RFC2445_DATE_LENGTH = 15;

	public static Time now;

	public static String[] months = { "January", "February", "March", "April",
			"May", "June", "July", "August", "September", "October",
			"November", "December" };
	public static String[] days = { "Sunday", "Monday", "Tuesday", "Wednesday",
			"Thursday", "Friday", "Saturday" };

	public static String DEFAULT_OVERRIDE_NAME = "-";

	/**
	 * Updates the current time in local variables {@link now}.
	 * 
	 * @return the Time object that is the current time.
	 */
	public static Time updateCurrentTime() {
		now = new Time();
		now.setToNow();
		now.normalize(false);
		return now;
	}

	/**
	 * Updates the current time and returns a {@code ScheduleTime} object of the
	 * current time.
	 * 
	 * @return a ScheduleTime object of the current time
	 */
	public static STime getCurrentScheduleTime() {
		return new STime(now.hour, now.minute);
	}

	/**
	 * Shifts the day of the given time by the number of days to shift
	 * 
	 * @param time
	 *            given time
	 * @param dayChange
	 *            number of days to shift time
	 * @return shifted time
	 */
	public static Time shiftDay(Time time, int dayChange, SData data) {
		time.set(0, data.getMiscDetail("shifted_min"),
				data.getMiscDetail("shifted_hour"), time.monthDay + dayChange,
				time.month, time.year);
		time.normalize(false);
		return time;
	}

	/**
	 * Returns the standard String form of the given time.
	 */
	public static String getDateString(Time t) {
		String s = months[t.month] + " " + t.monthDay + ", " + t.year;
		return s;
	}

	/**
	 * Returns the standard String form of the given time.
	 */
	public static String getDateString(String s) {
		Time x = new Time();
		x.parse(s);
		x.normalize(false);
		return getDateString(x);
	}

	/**
	 * Returns the day of the week of the given time as a String.
	 * 
	 * @return the day of the week ex. "Monday"
	 */
	public static String getDay(Time scheduleDay) {
		return days[scheduleDay.weekDay];
	}

	/**
	 * Gets Julian Day of the given Time.
	 * 
	 * @return Julian Day
	 */
	public static int getJulianDay(Time t) {
		long m = t.toMillis(false);
		long g = t.gmtoff;
		return Time.getJulianDay(m, g);
	}

	public static boolean areArraysEqual(String[] a, String[] b) {
		if (a.length != b.length)
			return false;
		for (int i = 0; i < a.length; i++) {
			if (!a[i].equals(b[i]))
				return false;
		}
		return true;
	}

	public static ArrayList<String> getArrayListFromArray(String[] s) {
		ArrayList<String> a = new ArrayList<String>();
		for(int i = 0; i < s.length; i++) {
			a.add(s[i]);
		}
		return a;
	}

	public static String shortenGrp(String string) {
		string = string.substring(string.indexOf(" ") + 1);
		return (string = string.substring(string.indexOf(" ") + 1));
	}

	public static Time getTimeFromString(String s) {
		Time t = new Time();
		try {
			t.parse(s);
			t.normalize(false);
		} catch (Exception e) {
			t.setToNow();
		}
		return t;
	}
}
