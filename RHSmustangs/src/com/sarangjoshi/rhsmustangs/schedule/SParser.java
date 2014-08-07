/**
 * SParser.java
 * May 12, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.text.format.Time;

public class SParser {
	private Context mContext;
	private SNetwork mNetwork;
	private SData mData;

	/**
	 * The current day that the schedule is showing.
	 */
	private Time scheduleDay;

	private Time lastUpdate;
	private Time lastHolUpdate;
	private String[] updatedDays;
	private ArrayList<String> holidays = new ArrayList<String>();
	private boolean isUpdated;

	public SParser(Context newContext) {
		mContext = newContext;
		mData = new SData(mContext);
		mNetwork = new SNetwork();
	}

	// UPDATES FILE
	/**
	 * Downloads and saves the entire online file to a local String variable.
	 * 
	 * @return whether or not the file has been updated
	 */
	public boolean saveAndParseUpdatesFile() {
		String lTime = mNetwork.getLatestUpdateTime();
		if (lTime.equals(mData.getUpdateTime()))
			return false; // no updates
		else {
			// STEP 1: Saves updates
			boolean a = mData.saveUpdates(mNetwork.getUpdatesFileText());
			// STEP 2: Parses
			parseUpdatesFile();
			// STEP 3: Saves group names
			try {
				String write = "";
				for (String s : updatedDays) {
					String[] lines = s.split("\n");
					String day = lines[0];
					String currentWrite = "";
					for (int i = 1; i < lines.length; i++) {
						if (lines[i].substring(0, lines[i].indexOf(" "))
								.equals("GRP")) {
							// Parses out the actual group name
							String grpName = lines[i].substring(lines[i]
									.indexOf(" ") + 1);
							currentWrite += day + " " + i + " " + grpName
									+ "\n";
						} else
							break;
					}
					// Writes the string to the file
					if (currentWrite.trim().length() > 0)
						write += currentWrite;
				}
				if (write.trim().length() > 0)
					a = a && mData.savePeriodGroups(write);
			} catch (Exception e) {
			}
			return a;
		}
	}

	// Online parsing
	public boolean saveAndParseHolidaysFile() {
		String lTime = mNetwork.getHolidaysUpdateTime();
		if (lTime.equals(mData.getHolidaysUpdateTime()))
			return false; // no updates
		else {
			boolean a = true;
			// Download and parse online file into file strings
			String t = mNetwork.getHolidaysFileText();
			String[] holidayFileStrings = null;
			if (!(t.contains("<") || t.contains(">")) || t.trim().length() == 0)
				holidayFileStrings = null;
			else {
				try {
					// MAIN PARSING OF THE SAVED FILE
					// STEP 1: Gets update time - first line of text file
					String lastHolUpdateTimeString = t.substring(0,
							t.indexOf('\n'));
					// Saves update time to SData
					mData.saveHolidaysUpdateTime(lastHolUpdateTimeString);
					// Parses string to Time object
					if (lastHolUpdateTimeString.length() > 8) {
						// Removes first line
						t = t.substring(t.indexOf('\n') + 1);
						lastHolUpdate = SStatic
								.getTimeFromString(lastHolUpdateTimeString);
					} else
						lastHolUpdate = null;

					// STEP 2: Parse into individual schedule days
					// Removes the beginning < and ending >
					t = t.substring(t.indexOf('<') + 2, t.lastIndexOf('>') - 1);
					// Now splitting the string into various arrays by
					// "\n>\n<\n"
					holidayFileStrings = t.split("\n>\n<\n");
				} catch (Exception e) {
					holidayFileStrings = null;
				}
			}
			// Save prefs
			try {
				for (String s : holidayFileStrings) {
					String[] lines = s.split("\n");
					mData.deleteAllHolidays();
					// Strings
					String start = lines[0];
					String end = lines[1].substring(lines[1].indexOf(" ") + 1);
					String name = lines[2];
					// Times
					Time startT = SStatic.getTimeFromString(start);
					Time endT = SStatic.getTimeFromString(end);
					a = a && mData.saveHoliday(startT, name);
					while (SStatic.getJulianDay(startT) != SStatic
							.getJulianDay(endT)) {
						startT.monthDay++;
						startT.normalize(false);
						a = a && mData.saveHoliday(startT, name);
					}
				}
			} catch (Exception e) {
			}
			// Offline parsing
			return a && readHolidays();
		}		
	}

	/**
	 * Offline reading from prefs
	 * 
	 * @return
	 */
	public boolean readHolidays() {
		// Read from prefs
		try {
			Object[] prefDaysArray = mData.getAllHolidays().keySet().toArray();
			holidays = new ArrayList<String>();
			// Set<String> prefDays = prefHols.keySet(); = prefDays.toArray();
			for (int i = 0; i < prefDaysArray.length; i++) {
				holidays.add((String) prefDaysArray[i]);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
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
				// STEP 1: Gets update time - first line of text file
				String lastUpdateString = updateS.substring(0,
						updateS.indexOf('\n'));
				// Saves update time to SData
				mData.saveUpdateTime(lastUpdateString);
				// Parses string to Time object
				if (lastUpdateString.length() > 8) {
					// Removes first line
					updateS = updateS.substring(updateS.indexOf('\n') + 1);
					lastUpdate = SStatic.getTimeFromString(lastUpdateString);
				} else
					lastUpdate = null;

				// STEP 2: Parse into individual schedule days
				// Removes the beginning < and ending >
				updateS = updateS.substring(updateS.indexOf('<') + 2,
						updateS.lastIndexOf('>') - 1);
				// Now splitting the string into various arrays by "\n>\n<\n"
				updatedDays = updateS.split("\n>\n<\n");
			} catch (Exception e) {
				updatedDays = null;
			}
		}
	}

	// SCHEDULE RETRIEVING
	/**
	 * Parses and returns a set of Periods, based on the current value of
	 * {@link scheduleDay}.
	 * 
	 * @return the timetable
	 */
	public ArrayList<Period> getPeriods() {
		ArrayList<Period> periods = new ArrayList<Period>();

		String holName = mData.getHolidayName(scheduleDay.toString().substring(
				0, 8));

		// Check for holidays
		if (holName != null) {
			Period p = new Period("HD", holName, 0, 0, 11, 59, 0);
			periods.add(p);
			isUpdated = true;
		} else {
			// Gets current schedule
			String currentSchedule = getSchedule(scheduleDay);

			// Parses out individual period strings
			String[] result = currentSchedule.split("\n");

			// Loads individual strings into the ArrayList, depending on whether
			// the
			// lunch is correct
			for (int i = 0; i < result.length; i++) {
				if (!result[i].substring(0, result[i].indexOf(" ")).equals(
						"GRP")) {
					Period p = getPeriodFromString(result[i]);
					// char lunch = mData.getLunch();
					// if (p.lunchStyle == '0' || p.lunchStyle == lunch)
					// periods.add(p);
					int grp = mData.getGroupPref(todayShort());
					if (p.groupN == grp || p.groupN == 0)
						periods.add(p);
				}
			}
		}

		return periods;
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
		int adjustedIndex = dayAdjustedIndex(day);
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
		if (overrideName.equals(SStatic.DEFAULT_OVERRIDE_NAME)) {
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

		// length-1 Group Number
		try {
			p.groupN = Integer.parseInt(result[result.length - 1]);
		} catch (Exception e) {
			p.groupN = 0;
		}

		return p;
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
		try {
			if (updatedDays != null) {
				// Go through adjustedDaysText to find the correct day
				for (int i = 0; i < updatedDays.length; i++) {
					String s = updatedDays[i];
					String l = s.substring(0, s.indexOf('\n'));

					Time x = new Time();
					x.parse(l);
					x.normalize(false);
					if (SStatic.getJulianDay(x) == SStatic.getJulianDay(day)) {
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
	}

	/**
	 * Updates the local scheduleDay variable to the appropriate time, given the
	 * desired time.
	 * 
	 * @param now
	 *            the day to set scheduleDay. Default: SStaticData.now
	 * @param isForward
	 *            whether the schedule is moving forward
	 */
	public void updateScheduleDay(Time now, boolean isForward) {
		// scheduleDay reflects whatever schedule is being shown
		scheduleDay = now;

		// Get day and hour based on the given Time object
		int day = scheduleDay.weekDay;
		int hour = scheduleDay.hour;

		// If the time is more than 2 hours after the end time, and it's a
		// weekday, it shifts forward one day.
		if (isForward)
			if (day > Time.SUNDAY)
				if (day < Time.SATURDAY)
					if (hour >= 2 + ((day == Time.WEDNESDAY) ? mData
							.getMiscDetail("endHrW") : mData
							.getMiscDetail("endHr"))) {
						scheduleDay = SStatic.shiftDay(scheduleDay, 1, mData);
					}

		day = scheduleDay.weekDay;
		hour = scheduleDay.hour;

		// Both Saturday and Sunday go to the next Monday
		if (day == Time.SATURDAY) {
			scheduleDay = SStatic.shiftDay(scheduleDay, (isForward ? 2 : -1),
					mData);
		} else if (day == Time.SUNDAY) {
			scheduleDay = SStatic.shiftDay(scheduleDay, (isForward ? 1 : -2),
					mData);
		}

		// Adjusts the other variables in the time object
		scheduleDay.normalize(false);

		// Saves scheduleDay
		mData.saveLatestDay(scheduleDay.toString().substring(0, 8));
	}

	/*
	 * Based on {@link SData}'s lunch character, returns a string for the
	 * spinner adapter.
	 * 
	 * E.g. if lunch was 'a' then this returns "Lunch A".
	 * 
	 * @return the string for the spinner adapter
	 * 
	 * public String getSpinnerLunch() { int x = (int) mData.getLunch(); String
	 * s = "Lunch " + (char) (x - 32); return s; }
	 * 
	 * 
	 * /** Sets the lunch in SData to the selected lunch based on the position
	 * of the click
	 * 
	 * @param position the position of the click, 0 = a, 1 = b, 2 = c
	 * 
	 * @return whether the lunch was successfully selected
	 * 
	 * public boolean lunchSelected(int position) { mData.setLunch((char) (97 +
	 * position)); return true; }
	 */

	// SCHEDULE OBJECTS
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
		SStatic.updateCurrentTime();
		try {
			int diff = SStatic.getJulianDay(SStatic.now)
					- SStatic.getJulianDay(scheduleDay);
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
		return SStatic.getDateString(scheduleDay);
	}

	/**
	 * Updates the local {@link scheduleDay} variable by shifting it by
	 * {@link d} days
	 * 
	 * @param d
	 *            the change in days
	 */
	public void shiftDay(int d) {
		updateScheduleDay(SStatic.shiftDay(scheduleDay, d, mData), (d >= 0));
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

	/**
	 * Saves misc details.
	 * 
	 * @return
	 */
	public boolean saveMiscDetails() {
		String det = mNetwork.getMisc();
		String[] details = det.split("\n");
		String[] keyValPair = new String[2];
		boolean a = true;
		for (int i = 0; i < details.length; i++) {
			keyValPair = details[i].split(" ");
			try {
				a = a
						&& mData.saveMiscDetail(keyValPair[0],
								Integer.parseInt(keyValPair[1]));
			} catch (Exception e) {
				a = a && false;
			}
		}
		return a;
	}

	/**
	 * Deletes updates, deletes base schedules.
	 */
	public void resetEverything() {
		mData.saveInitialize(false);
		mData.deletePeriods();
		mData.deleteSavedUpdates();
		mData.deleteBase();
	}

	/**
	 * Gets the spinner values for the current {@link scheduleDay}.
	 * 
	 * @return Spinner values array. null if no period groups for scheduleDay.
	 */
	public String[] getSpinnerValues() {
		if (dayAdjustedIndex(scheduleDay) >= 0) {
			String[] spinValues = mData.getPeriodGroups(todayShort());
			for (int i = 0; i < spinValues.length; i++) {
				spinValues[i] = SStatic.shortenGrp(spinValues[i]);
			}
			return spinValues;
		}
		return null;
	}

	/**
	 * Gets the currently selected group number for the current schedule.
	 * 
	 * @return the groupN, 1-n
	 */
	public int getSelectedGroupN() {
		return mData.getGroupPref(todayShort());
	}

	/**
	 * Sets the group number for the current scheduleDay.
	 * 
	 * @param groupN
	 */
	public void groupSelected(int groupN) {
		String today = scheduleDay.toString().substring(0, 8);
		mData.saveGroupPref(today, groupN);
	}

	private String todayShort() {
		return scheduleDay.toString().substring(0, 8);
	}

	public void setToLatestDay(boolean isForward) {
		Time t = new Time();
		String s = mData.getLatestDay();
		if (!s.equals("")) {
			t.parse(s);
			t.normalize(false);
		} else {
			t = SStatic.now;
		}
		updateScheduleDay(t, isForward);
	}
}
