/**
 * ScheduleStaticData.java
 * May 23, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import android.text.format.Time;

public class SStaticData {
	public static String a = "P1 07 30 08 24\nP2 08 30 09 24\nP3 09 30 10 24\nLA 10 30 11 00\nP4 11 06 12 00\nP5 12 06 13 00\nP6 13 06 14 00\n";
	public static String b = "P1 07 30 08 24\nP2 08 30 09 24\nP3 09 30 10 24\n4A 10 30 11 00\nLB 11 00 11 30\n4B 11 36 12 00\nP5 12 06 13 00\nP6 13 06 14 00\n";
	public static String c = "P1 07 30 08 24\nP2 08 30 09 24\nP3 09 30 10 24\nP4 10 30 11 24\nLC 11 30 12 00\nP5 12 06 13 00\nP6 13 06 14 00\n";

	public static String wed = "P1 07 30 08 10\nP2 08 16 08 56\nP3 09 02 09 42\nP4 09 48 10 28\nP5 10 34 11 14\nP6 11 20 12 00\nLN 12 00 12 30\n";

	public static String ta = "P1 07 30 08 20\nP2 08 26 09 16\nHR 09 22 09 46\nP3 09 46 10 36\nLA 10 42 11 12\nP4 11 18 12 08\nP5 12 14 13 04\nP6 13 10 14 00\n";
	public static String tb = "P1 07 30 08 20\nP2 08 26 09 16\nHR 09 22 09 46\nP3 09 46 10 36\n4A 10 42 11 07\nLB 11 07 11 37\n4B 11 43 12 08\nP5 12 14 13 04\nP6 13 10 14 00\n";
	public static String tc = "P1 07 30 08 20\nP2 08 26 09 16\nHR 09 22 09 46\nP3 09 46 10 36\nP4 10 42 11 32\nLC 11 38 12 08\nP5 12 14 13 04\nP6 13 10 14 00\n";

	public static int passing_period = 6;
	public static int start_hour = 7;
	public static int start_minutes = 30;

	public static Time now;
	// All sub-variables of {@link now}
	public static int nowHour;
	public static int nowDay;
	public static int nowMinute;

	public static String[] months = { "January", "February", "March", "April",
			"May", "June", "July", "August", "September", "October",
			"November", "December" };

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
	public static String getScheduleByDay(int day, char lunch) {
		if (day == Time.SATURDAY || day == Time.SUNDAY)
			day = Time.MONDAY;

		if (day == Time.WEDNESDAY)
			return wed;
		else if (day == Time.THURSDAY)
			switch (lunch) {
			case ('b'):
				return tb;
			case ('c'):
				return tc;
			default:
				return ta;
			}
		else
			switch (lunch) {
			case ('b'):
				return b;
			case ('c'):
				return c;
			default:
				return a;
			}
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
	 * Updates the current time in local variables {@link now}, {@link hour},
	 * and {@link minute}.
	 * 
	 * @return the Time object that is the current time.
	 */
	public static Time updateCurrentTime() {
		now = new Time();
		now.setToNow();
		now.set(0, 10, 4, 8, 5, 2014);
		now.normalize(false);
		nowHour = now.hour;
		nowMinute = now.minute;
		nowDay = now.weekDay;
		return now;
	}

	/**
	 * Updates the current time and returns a {@code ScheduleTime} object of the
	 * current time.
	 * 
	 * @return a ScheduleTime object of the current time
	 */
	public static ScheduleTime getCurrentScheduleTime() {
		return new ScheduleTime(nowHour, nowMinute);
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
	 * @param time given time
	 * @param dayChange number of days to shift time
	 * @return shifted time
	 */
	public static Time shiftDay(Time time, int dayChange) {
		time.set(time.second, time.minute, time.hour,
				time.monthDay + dayChange, time.month, time.year);
		time.normalize(false);
		return time;
	}

	/**
	 * Returns the standard String form of the given time.
	 */
	public static String getDateString(Time t) {
		String s =  months[t.month] + " " + t.monthDay + ", " + t.year;
		return s;
	}
}
