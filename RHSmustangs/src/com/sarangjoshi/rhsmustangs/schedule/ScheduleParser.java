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
import android.text.format.Time;

public class ScheduleParser {
	private String currentSchedule = null;
	private char lunch = 'c';

	/*
	 * public void createAndSaveFiles() { FileOutputStream stream; try { stream
	 * = mContext.openFileOutput(filenames[0], Context.MODE_PRIVATE); try {
	 * stream.write(s.getBytes()); stream.close(); } catch (IOException e) { //
	 * TODO solve } } catch (FileNotFoundException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); }
	 * 
	 * }
	 * 
	 * public void loadFromFile() { FileInputStream stream; byte[] buffer; try {
	 * stream = mContext.openFileInput(filenames[0]); try { stream.read(buffer,
	 * 0, );
	 * 
	 * } catch (IOException e) {
	 * 
	 * } } catch (FileNotFoundException e) {
	 * 
	 * } }
	 */

	/**
	 * Gets the current day and sets the local variable
	 */
	public void updateCurrentDay() {
		// Sunday = 1
		int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);;
		
		ScheduleData.now = new Time();
		ScheduleData.now.setToNow();
		
		int hour = ScheduleData.now.hour;
		
		if(day == Calendar.SATURDAY || day == Calendar.SUNDAY)
			currentSchedule = ScheduleData.getScheduleByDay(Calendar.MONDAY, lunch);			
		else if (hour >= 2 + ScheduleData.getEndHour(day)) 
			currentSchedule = ScheduleData.getScheduleByDay(day + 1, lunch);
		else
			currentSchedule = ScheduleData.getScheduleByDay(day, lunch);
	}
	
	/**
	 * Based on the local string {@link s}, parses and returns a set of Periods.
	 * 
	 * @return the timetable
	 */
	public ArrayList<Period> getPeriods() {
		ArrayList<Period> periods = new ArrayList<Period>();
		if (currentSchedule == null)
			updateCurrentDay();

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
}
