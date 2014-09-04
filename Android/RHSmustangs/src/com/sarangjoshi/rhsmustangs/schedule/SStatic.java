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

	public static String lunch_short = "LN";
	public static String hr_short = "HR";
	public static String COLOR_UPDATE = "#006600";
	public static String COLOR_HOLIDAY = "#D4AF37";//"#FFD700";

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

	public static String getTimeString(Time t) {
		String s = months[t.month] + " " + t.monthDay + ", " + t.year + " ";
		s += t.hour + ":" + ((t.minute < 10) ? "0" + t.minute : t.minute);
		return s;
	}

	public static String getTimeString(String s) {
		return getTimeString(getTimeFromString(s, 15));
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
		return getDateString(getTimeFromString(s, 8));
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
		for (int i = 0; i < s.length; i++) {
			a.add(s[i]);
		}
		return a;
	}

	public static String shortenCustomGrp(String string) {
		string = string.substring(string.indexOf(" ") + 1);
		return (string = string.substring(string.indexOf(" ") + 1));
	}

	public static Time getTimeFromString(String s, int len) {
		Time t = new Time();
		if (s.length() > len)
			s = s.substring(0, len);
		try {
			t.parse(s);
			t.normalize(false);
		} catch (Exception e) {
			t.setToNow();
		}
		return t;
	}

	public static Time absShiftDay(Time time, int i) {
		time.set(0, time.minute, time.hour, time.monthDay + i, time.month,
				time.year);
		time.normalize(false);
		return null;
	}
}
