/**
 * ScheduleStaticData.java
 * May 23, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import android.text.format.Time;

public class SStaticData {
	public static String norm = "1 - 07 30 08 24 0\n2 - 08 30 09 24 0\n3 - 09 30 10 24 0\nLA - 10 30 11 00 a\n4 - 11 06 12 00 a\n4A - 10 30 11 00 b\nLB - 11 00 11 30 b\n4B - 11 36 12 00 b\n4 - 10 30 11 24 c\nLC - 11 30 12 00 c\n5 - 12 06 13 00 0\n6 - 13 06 14 00 0\n";
	public static String wed = "1 - 07 30 08 10 0\n2 - 08 16 08 56 0\n3 - 09 02 09 42 0\n4 - 09 48 10 28 0\n5 - 10 34 11 14 0\n6 - 11 20 12 00 0\nLN - 12 00 12 30 0\n";
	public static String t = "1 - 07 30 08 20 0\n2 - 08 26 09 16 0\nHR - 09 22 09 46 0\n3 - 09 46 10 36 0\nLA - 10 42 11 12 a\n4 - 11 18 12 08 a\n4A - 10 42 11 07 b\nLB - 11 07 11 37 b\n4B - 11 43 12 08 b\n4 - 10 42 11 32 c\nLC - 11 38 12 08 c\n5 - 12 14 13 04 0\n6 - 13 10 14 00 0\n";

	public static int passing_period = 6;
	public static int start_hour = 7;
	public static int start_minutes = 30;

	public static int shifted_hour = 9;
	public static int shifted_min = 15;

	public static Time now;

	public static String[] months = { "January", "February", "March", "April",
			"May", "June", "July", "August", "September", "October",
			"November", "December" };
	public static String[] days = { "Sunday", "Monday", "Tuesday", "Wednesday",
			"Thursday", "Friday", "Saturday" };
	
	public static String defaultOverrideName = "-";

	/**
	 * Given the current day and lunch type, returns the appropriate schedule
	 * string.
	 * 
	 * @param day
	 *            the current weekday
	 * @param lType
	 *            the lunch type
	 * @return the schedule string
	 */
	public static String getScheduleByDay(int day) {
		if (day == Time.SATURDAY || day == Time.SUNDAY)
			day = Time.MONDAY;

		if (day == Time.WEDNESDAY)
			return wed;
		else if (day == Time.THURSDAY)
			return t;
		else
			return norm;
	}

	/**
	 * Gets the end hour given a day of the week
	 * 
	 * @param day
	 *            the day of the week
	 * @return the hour at which school ends
	 */
	public static int getEndHour(int day) {
		if (day == Time.WEDNESDAY)
			return 12;
		else
			return 14;
	}

	/**
	 * Gets the end minutes given a day of the week
	 * 
	 * @param day
	 *            the day of the week
	 * @return the hour at which school ends
	 */
	public static int getEndMinutes(int day) {
		if (day == Time.WEDNESDAY)
			return 30;
		else
			return 0;
	}

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
	public static ScheduleTime getCurrentScheduleTime() {
		return new ScheduleTime(now.hour, now.minute);
	}

	/**
	 * Returns difference between two days. Positive if the second time is after
	 * the first. Effectively b - a.
	 * 
	 * @param a
	 *            first day
	 * @param b
	 *            second day
	 * @return
	 */
	public static int dayDifference(Time a, Time b) {
		return (b.yearDay - a.yearDay) + 365 * (b.year - a.year);
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
	public static Time shiftDay(Time time, int dayChange) {
		time.set(0, shifted_min, shifted_hour, time.monthDay + dayChange,
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
}
