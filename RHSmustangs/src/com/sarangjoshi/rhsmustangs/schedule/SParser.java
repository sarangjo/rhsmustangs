/**
 * SParser.java
 * May 12, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import java.util.ArrayList;

import android.content.Context;
import android.text.format.Time;

public class SParser {
	private String currentSchedule = null;
	private Context mContext;
	private SNetwork mNetwork;

	private SData mData;

	private Time scheduleDay;

	private String fileText;
	private String[] adjustedDaysText;

	public SParser(Context newContext) {
		mContext = newContext;
		mData = new SData(mContext);
		mNetwork = new SNetwork(mContext);
		
		parseFileData();
	}
	
	/**
	 * Downloads and saves the entire online file to a local String variable.
	 * 
	 * @return whether or not the file had already been saved
	 */
	public boolean saveFileData() {
		if (fileText == null) {
			fileText = mNetwork.getFileText();
			return true;
		} else
			return false;
	}

	/**
	 * Parses the local variable fileText into blocks of text, and stores in
	 * {@link adjustedDaysText}.
	 */
	public void parseFileData() {
		saveFileData();
		// Current method:
		String s = fileText;
		// First, removes the beginning < and ending >
		try {
			s = s.substring(2, s.length() - 3);
			// Now splitting the string into various arrays by "\n>\n<\n"
			adjustedDaysText = s.split("\n>\n<\n");
		} catch (IndexOutOfBoundsException e) {
			adjustedDaysText = null;
		}
	}

	/**
	 * Parses and returns a set of Periods, based on the current value of
	 * {@link scheduleDay}.
	 * 
	 * @return the timetable
	 */
	public ArrayList<Period> getPeriods() {
		ArrayList<Period> periods = new ArrayList<Period>();

		// Gets current schedule
		currentSchedule = getSchedule(scheduleDay);

		// Parses out individual period strings
		String[] result = currentSchedule.split("\n");

		// Loads individual strings into the ArrayList, depending on whether the
		// lunch is correct
		for (int i = 0; i < result.length; i++) {
			Period p = getPeriodFromString(result[i]);
			char lunch = mData.getLunch();
			if (p.lunchStyle == '0' || p.lunchStyle == lunch)
				periods.add(p);
		}

		return periods;
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
		// Period Number
		p.mPeriodShort = result[0];

		// Period Name
		p.mClassName = mData.getPeriodName(p);

		// Start Time
		int sHours = Integer.parseInt(result[1]);
		int sMin = Integer.parseInt(result[2]);
		p.mStartTime = new ScheduleTime(sHours, sMin);

		// End Time
		int eHours = Integer.parseInt(result[3]);
		int eMin = Integer.parseInt(result[4]);
		p.mEndTime = new ScheduleTime(eHours, eMin);

		// Lunch Style
		p.lunchStyle = result[5].charAt(0);

		return p;
	}

	/**
	 * Given a day of the year, returns the correct schedule for that day. If
	 * the schedule is adjusted, returns the parsed-out adjusted schedule from
	 * the fileText. If the given day is not adjusted, returns the standard
	 * non-adjusted schedule.
	 * 
	 * @param day
	 *            the day of the adjusted schedule
	 * @return
	 */
	private String getSchedule(Time day) {
		// Checks if the day is adjusted or not
		int x = dayAdjustedIndex(day);
		if (x >= 0) {
			// From the array of adjusted days, gets schedule
			String sched = adjustedDaysText[x];
			return sched.substring(sched.indexOf('\n') + 1, sched.length());
		} else
			// Pulls appropriate schedule based on current day
			return SStaticData.getScheduleByDay(scheduleDay.weekDay);

	}	

	/**
	 * <b>Call parseFileData() before this.</b>
	 * <p>
	 * Returns the index among all the adjusted schedules of the given day. If
	 * it is not adjusted, then returns -1.
	 * </p>
	 * 
	 * @return whether or not the given day has an adjusted schedule
	 */
	private int dayAdjustedIndex(Time day) {
		int i = -1;

		if (adjustedDaysText != null) {
			// Go through adjustedDaysText to find the correct day
			for (i = 0; i < adjustedDaysText.length; i++) {
				String s = adjustedDaysText[i];
				String l = s.substring(0, s.indexOf('\n'));
				String[] t = l.split(" ");
				int d = Integer.parseInt(t[0]);
				int m = Integer.parseInt(t[1]);
				int y = Integer.parseInt(t[2]);
				if (day.monthDay == d)
					if (day.month == m - 1)
						if (day.year == y)
							return i;
			}
		}
		return -1;
		/*
		 * String[] lines = fileText.split("\n"); // i for index int i; for (i =
		 * 0; i < lines.length; i++) { if (lines[i].contains("<")) { // The very
		 * next line contains the date of the adjusted schedule String l =
		 * lines[i + 1]; String[] t = l.split(" "); int d =
		 * Integer.parseInt(t[0]); int m = Integer.parseInt(t[1]); int y =
		 * Integer.parseInt(t[2]); if (day.monthDay == d) if (day.month == m -
		 * 1) if (day.year == y) return index; } if (lines[i].contains(">")) {
		 * index++; } } return index;
		 */
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
			scheduleDay = SStaticData.shiftDay(scheduleDay,
					(isForward ? 2 : -1));
		} else if (day == Time.SUNDAY) {
			scheduleDay = SStaticData.shiftDay(scheduleDay,
					(isForward ? 1 : -2));
		}

		// Adjusts the other variables in the time object
		scheduleDay.normalize(false);
	}

	/**
	 * Based on {@link SData}'s lunch character, returns a string for the spinner
	 * adapter.
	 * 
	 * E.g. if lunch was 'a' then this returns "Lunch A".
	 * 
	 * @return the string for the spinner adapter
	 */
	public String getSpinnerLunch() {
		int x = (int) mData.getLunch();
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
		mData.setLunch((char) (97 + position));
		return true;
	}

	/**
	 * Returns the SData object.
	 * 
	 * @return the SData object
	 */
	public SData getSData() {
		return mData;
	}

	/**
	 * Gets schedule title, relative to SStaticDay.now.
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
