/**
 * Parser.java
 * May 12, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import java.util.ArrayList;

import android.content.Context;
import android.text.format.Time;

public class SParser {
	private String currentSchedule = null;
	private Context context;

	private SData sData;

	private Time scheduleDay;

	public SParser(Context newContext) {
		context = newContext;
		sData = new SData(context);
	}

	/**
	 * Parses and returns a set of Periods, based on the current value of
	 * {@link scheduleDay}.
	 * 
	 * @return the timetable
	 */
	public ArrayList<Period> getPeriods() {
		ArrayList<Period> periods = new ArrayList<Period>();

		// Pulls appropriate schedule based on hardcoded values
		currentSchedule = SStaticData.getScheduleByDay(scheduleDay.weekDay,
				sData.getLunch());

		// Parses out individual period strings
		String[] result = currentSchedule.split("\n");

		// Loads individual strings into the ArrayList
		for (int i = 0; i < result.length; i++) {
			periods.add(getPeriodFromString(result[i]));
		}

		return periods;
	}

	/**
	 * Updates the local scheduleDay variable to the appropriate time, given the
	 * desired time.
	 * 
	 * @param now
	 *            the day to set scheduleDay. Default: SStaticData.now
	 * @param direction
	 *            the direction in which the user is navigating
	 */
	public void updateScheduleDay(Time now, boolean isForward) {
		// scheduleDay reflects whatever schedule is being shown
		scheduleDay = now;

		// Get day and hour based on the given Time object
		int day = scheduleDay.weekDay;
		int hour = scheduleDay.hour;

		// If the time is more than 2 hours after the current time, and it's a
		// weekday, it shifts forward one day.
		if (isForward && day > Time.SUNDAY && day < Time.SATURDAY
				&& hour >= 2 + SStaticData.getEndHour(day)) {
			scheduleDay = SStaticData.shiftDay(scheduleDay, 1);
		}

		// Both Saturday and Sunday go to the next Monday
		if (day == Time.SATURDAY) {
			scheduleDay = SStaticData.shiftDay(scheduleDay, (isForward ? 2 : -1));
		} else if (day == Time.SUNDAY) {
			scheduleDay = SStaticData.shiftDay(scheduleDay, (isForward ? 1 : -2));
		}

		// Adjusts the other variables in the time object
		scheduleDay.normalize(false);
	}

	/**
	 * Parsing string into Period
	 * 
	 * @param str
	 * @return
	 */
	private Period getPeriodFromString(String str) {
		Period p = new Period();
		String[] result = str.split(" ");
		try {
			// Period Number
			p.mPeriodShort = result[0];

			// Period Name
			p.mClassName = sData.getPeriodName(p);

			// Start Time
			int sHours = Integer.parseInt(result[1]);
			int sMin = Integer.parseInt(result[2]);
			p.mStartTime = new ScheduleTime(sHours, sMin);
			// End Time
			int eHours = Integer.parseInt(result[3]);
			int eMin = Integer.parseInt(result[4]);
			p.mEndTime = new ScheduleTime(eHours, eMin);
		} catch (ArrayIndexOutOfBoundsException e) {

		}
		return p;
	}

	/**
	 * Based on {@link SData}'s lunch character, returns a string for th spinner
	 * adapter.
	 * 
	 * E.g. if lunch was 'a' then this returns "Lunch A".
	 * 
	 * @return the string for the spinner adapter
	 */
	public String getLunchForAdapter() {
		int x = (int) sData.getLunch();
		String s = "Lunch " + (char) (x - 32);
		return s;
	}

	/**
	 * Sets the lunch in SData to the selected lunch based on the position of
	 * the click
	 * 
	 * @param position
	 *            the position of the click, 0 = a, 1 = b, 2 = c
	 * @return whether the lunch was successfully selected
	 */
	public boolean lunchSelected(int position) {
		sData.setLunch((char) (97 + position));
		return true;
	}

	/**
	 * Returns the SData object.
	 * 
	 * @return the SData object
	 */
	public SData getSData() {
		return sData;
	}

	/**
	 * Gets schedule title, based on SStaticDay.now
	 * 
	 */
	public String getScheduleTitle() {
		int diff = SStaticData.dayDifference(scheduleDay, SStaticData.now);
		// Today
		if (diff == 0) {
			return "Today";
		}
		// Tomorrow
		else if (diff == -1)
			return "Tomorrow";
		// Yesterday
		else if (diff == 1)
			return "Yesterday";
		// Else
		return SStaticData.getDateString(scheduleDay);
	}

	/**
	 * Updates the local {@link scheduleDay} variable by shifting it by
	 * {@link d} days
	 * 
	 * @param d
	 *            the change in days
	 */
	public void shiftDay(int d) {
		updateScheduleDay(SStaticData.shiftDay(scheduleDay, d), (d >= 0));
	}

	public Time getScheduleDay() {
		return scheduleDay;
	}

}
