/**
 * Parser.java
 * May 12, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.format.Time;

public class SParser {
	private String currentSchedule = null;
	private Context context;

	private SData sData;

	public SParser(Context newContext) {
		context = newContext;
		sData = new SData(context);
	}

	/**
	 * Gets the current day and sets the local variable.
	 */
	public void setSchedule() {
		// Sunday = 1
		SSData.updateCurrentTime();

		int day = SSData.getCurrentDay();
		int hour = SSData.hour;

		if (day == Calendar.SATURDAY || day == Calendar.SUNDAY)
			currentSchedule = SSData.getScheduleByDay(
					Calendar.MONDAY, sData.getLunch());
		else if (hour >= 2 + SSData.getEndHour(day))
			currentSchedule = SSData.getScheduleByDay(day + 1,
					sData.getLunch());
		else
			currentSchedule = SSData.getScheduleByDay(day,
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
			p.mPeriodNum = result[0];
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
	 * Returns a pair of characters at a given index.
	 * 
	 * @param x
	 *            the String
	 * @param index
	 *            the starting index
	 * @return the pair of characters
	 */
	private static String getDuetAt(String x, int index) {
		return x.substring(index, index + 2);
	}

	/**
	 * Based on {@link SData}'s lunch character, returns a string for th
	 * spinner adapter.
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
	 * Sets the lunch in SData to the selected lunch based on the position of the click
	 * 
	 * @param position the position of the click, 0 = a, 1 = b, 2 = c
	 * @return whether the lunch was successfully selected
	 */
	public boolean lunchSelected(int position) {
		sData.setLunch((char)(97 + position));
		return true;
	}
}
