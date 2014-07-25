/**
 * SParser.java
 * May 12, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.text.format.Time;

public class SParser {
	private String currentSchedule = null;

	private Context mContext;
	private SNetwork mNetwork;
	private SData mData;

	private Time scheduleDay;

	private Time lastUpdate;
	private String[] updatedDays;
	private boolean isUpdated;

	public SParser(Context newContext) {
		mContext = newContext;
		mData = new SData(mContext);
		mNetwork = new SNetwork();
	}

	/**
	 * Downloads and saves the entire online file to a local String variable.
	 * 
	 * @return whether or not the file has been updated
	 */
	public boolean saveUpdatesFile() {
		String lTime = mNetwork.getLatestUpdateTime();
		if (lTime.equals(mData.getUpdateTime()))
			return false; // no updates
		else
			return mData.saveUpdates(mNetwork.getUpdatesFileText());
	}

	/**
	 * Parses the data from the file obtained from mData into blocks of text,
	 * and stores in {@link updatedDays}. If there are no updated days saved,
	 * then {@link updatedDays} is null.
	 */
	public void parseUpdatesFile() {
		// Gets local file's updates string
		String updateS = "";
		try {
			updateS = mData.getUpdatesString();
		} catch (IOException e) {
		}
		if (!(updateS.contains("<") || updateS.contains(">"))
				|| updateS.trim().length() == 0)
			updatedDays = null;
		else {
			try {
				// MAIN PARSING OF THE SAVED FILE
				// Gets update time - first line of text file
				String lastUpdateString = updateS.substring(0,
						updateS.indexOf('\n'));
				// Saves update time to SData
				mData.saveUpdateTime(lastUpdateString);
				// Parses string to Time object
				lastUpdate = new Time();
				if (lastUpdateString.length() > 8) {
					// Removes first line
					updateS = updateS.substring(updateS.indexOf('\n') + 1);
					lastUpdate.parse(lastUpdateString);
				} else
					lastUpdate.setToNow();
				lastUpdate.normalize(false);
				// Removes the beginning < and ending >
				// updateS = updateS.substring(2, updateS.length() - 3);
				updateS = updateS.substring(updateS.indexOf('<') + 2,
						updateS.lastIndexOf('>') - 1);
				// Now splitting the string into various arrays by "\n>\n<\n"
				updatedDays = updateS.split("\n>\n<\n");
			} catch (Exception e) {
				updatedDays = null;
			}
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
		// 0 Period Number
		p.mPeriodShort = result[0];

		// 1 to length - 6 Period Name
		String overrideName = "";
		for (int i = 1; i < result.length - 6; i++) {
			overrideName += result[i] + " ";
		}
		overrideName += result[result.length - 6];
		if (overrideName.equals(SStaticData.defaultOverrideName)) {
			// no override
			p.mClassName = mData.getPeriodName(p);
			p.isCustomizable = true;
		} else {
			// yes override
			p.mClassName = overrideName;
			p.isCustomizable = false;
		}

		int sHours, sMin, eHours, eMin;
		try {
			// length-5,length-4 Start Time
			sHours = Integer.parseInt(result[result.length - 5]);
			sMin = Integer.parseInt(result[result.length - 4]);

			// length-3,length-2 End Time
			eHours = Integer.parseInt(result[result.length - 3]);
			eMin = Integer.parseInt(result[result.length - 2]);
		} catch (Exception e) {
			sHours = 8;
			sMin = 30;
			eHours = 9;
			eMin = 20;
		}

		p.mStartTime = new STime(sHours, sMin);
		p.mEndTime = new STime(eHours, eMin);

		// length-1 Lunch Style
		p.lunchStyle = result[result.length - 1].charAt(0);

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
		int adjustedIndex = dayAdjustedIndex(day);

		// Checks if the day is adjusted or not
		if (adjustedIndex >= 0) {
			isUpdated = true;
			// From the array of adjusted days, gets schedule
			String sched = updatedDays[adjustedIndex];
			return sched.substring(sched.indexOf('\n') + 1, sched.length());
		} else {
			isUpdated = false;
			// Pulls appropriate schedule based on current day
			// return SStaticData.getScheduleByDay(scheduleDay.weekDay);
			int wDay = day.weekDay;
			wDay = (wDay == Time.SATURDAY || wDay == Time.SUNDAY) ? wDay = Time.MONDAY
					: wDay;
			return mData.getBaseSchedule(wDay);
		}
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

		try {
			if (updatedDays != null) {

				// Go through adjustedDaysText to find the correct day
				for (i = 0; i < updatedDays.length; i++) {
					String s = updatedDays[i];
					String l = s.substring(0, s.indexOf('\n'));

					Time x = new Time();
					x.parse(l);
					x.normalize(false);
					if (SStaticData.getJulianDay(x) == SStaticData
							.getJulianDay(day)) {
						return i;
					}
					/*
					 * String[] t = l.split(" "); int d =
					 * Integer.parseInt(t[0]); int m = Integer.parseInt(t[1]);
					 * int y = Integer.parseInt(t[2]); if (day.monthDay == d) if
					 * (day.month == m - 1) if (day.year == y) return i;
					 */
				}
			}
		} catch (Exception e) {
			return -1;
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

		// If the time is more than 2 hours after the end time, and it's a
		// weekday, it shifts forward one day.
		if (isForward && day > Time.SUNDAY && day < Time.SATURDAY
				&& hour >= 2 + SStaticData.getEndHour(day)) {
			scheduleDay = SStaticData.shiftDay(scheduleDay, 1);
		}

		day = scheduleDay.weekDay;
		hour = scheduleDay.hour;
		
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
	 * Based on {@link SData}'s lunch character, returns a string for the
	 * spinner adapter.
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

	public SNetwork getSNetwork() {
		return mNetwork;
	}

	/**
	 * Gets schedule title, relative to SStaticDay.now.
	 * 
	 */
	public String getScheduleTitle() {
		SStaticData.updateCurrentTime();
		try {
			int diff = SStaticData.getJulianDay(SStaticData.now)
					- SStaticData.getJulianDay(scheduleDay);
			// SStaticData.dayDifference(scheduleDay, SStaticData.now);
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
		} catch (Exception e) {
		}
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

	/**
	 * @return whether or not the current schedule is altered or not.
	 */
	public boolean isScheduleAdjusted() {
		return isUpdated;
	}

	/**
	 * Parses through the local variable {@link adjustedDaysText} and returns an
	 * array of just the dates of altered schedules.
	 */
	public String[] getUpdatedDays() {
		parseUpdatesFile();
		if (updatedDays != null) {
			String[] alteredDays = new String[updatedDays.length];

			for (int i = 0; i < updatedDays.length; i++) {
				String fullText = updatedDays[i];
				alteredDays[i] = fullText.substring(0, fullText.indexOf('\n'));
			}
			return alteredDays;
		} else
			return null;
	}

	/**
	 * Sets the schedule given an index of altered schedules.
	 * 
	 * @param index
	 *            index of the chosen altered schedule
	 */
	public void setAltDay(int index) {
		Time t = new Time();
		try {
			t.parse(getUpdatedDays()[index]);
			t.normalize(false);
			scheduleDay = t;
		} catch (Exception e) {
		}
	}

	/**
	 * Saves the base schedule.
	 */
	public boolean downloadBaseSchedules() {
		for (int i = Time.MONDAY; i <= Time.FRIDAY; i++) {
			String schedule = mNetwork.getBaseDay(i);

			if (schedule == "" || schedule == null) {
				return false;
			} else {
				mData.saveBaseDay(i, schedule);
			}
		}
		return true;
	}

	public boolean deleteUpdates() {
		boolean a = mData.deleteSavedUpdates();

		updatedDays = null;
		isUpdated = false;

		return a;
	}
}
