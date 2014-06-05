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
	 * Gets the current day and sets the local variable.
	 */
	public void setSchedule() {
		scheduleDay = new Time();
		scheduleDay.set(SStaticData.updateCurrentTime());

		// These are current day and hour
		int day = SStaticData.nowDay;
		int hour = SStaticData.nowHour;

		// Updated "day" to compare against for whether to shift forward for the
		// weekend
		day = scheduleDay.weekDay;

		// If the time is more than 2 hours after the current time, and it's a
		// weekday, it shifts forward one day.
		if (day > Time.SUNDAY && day < Time.SATURDAY
				&& hour >= 2 + SStaticData.getEndHour(day)) {
			scheduleDay = SStaticData.shiftDay(scheduleDay, 1);
		}

		// Both Saturday and Sunday go to the next Monday
		if (day == Time.SATURDAY) {
			scheduleDay = SStaticData.shiftDay(scheduleDay, 2);
		} else if (day == Time.SUNDAY) {
			scheduleDay = SStaticData.shiftDay(scheduleDay, 1);
		}

		scheduleDay.normalize(false);
		int dayToShow = scheduleDay.weekDay;

		currentSchedule = SStaticData.getScheduleByDay(dayToShow,
				sData.getLunch());
	}

	/**
	 * Based on the local string {@link s}, parses and returns a set of Periods.
	 * 
	 * @return the timetable
	 */
	public ArrayList<Period> getPeriods() {
		ArrayList<Period> periods = new ArrayList<Period>();
		setSchedule();

		String[] result = currentSchedule.split("\n");

		for (int i = 0; i < result.length; i++) {
			periods.add(getPeriodFromString(result[i]));
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
	 * The old method of parsing using iteration and String.charAt(int);.
	 * 
	 * @return the list of parsed periods.
	 */
	private ArrayList<Period> oldParseMethod() {
		int i = 0;
		ArrayList<Period> periods = new ArrayList<Period>();

		while (i < currentSchedule.length()) {
			String currentString = "";
			char c = currentSchedule.charAt(i);
			do {
				currentString += c;
				i++;
				c = currentSchedule.charAt(i);
			} while (c != '\n');
			i++;

			Period p = getPeriodFromString(currentString);

			periods.add(p);
		}

		return periods;
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
	 * Lolz
	 * 
	 * @return
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
}
