/**
 * SParser.java
 * May 12, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import java.util.ArrayList;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

public class SParser2 {
	// Class variables
	private Context mContext;
	private SNetwork mNetwork;
	private SData mData;

	/**
	 * The current day that's being shown.
	 */
	private SDay mToday;
	/**
	 * An array of all the updated schedule days.
	 */
	private SScheduleDay[] mUpdatedDays;
	/**
	 * An ArrayList of all the holiday periods.
	 */
	private ArrayList<SHolidayPeriod> mHolidays;

	/**
	 * Initializes all the basic class variables.
	 * 
	 * @param newContext
	 *            the Activity which is using the Parser.
	 */
	public SParser2(Context newContext) {
		mContext = newContext;
		mData = new SData(mContext);
		mNetwork = new SNetwork();
	}

	// UPDATES FILES
	/**
	 * INTERNET OPERATION<br>
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
				for (SDay day : mUpdatedDays) {
					// If the day isn't a SScheduleDay, then it exits the method
					SScheduleDay sDay = (SScheduleDay) day;
					write += sDay.getGroupsAsText();
				}
				// Trim and write to file
				if (write.trim().length() > 0)
					a = a && mData.savePeriodGroups(write);
			} catch (Exception e) {
				Log.d("grpSaving", "Exception in group saving.");
			}
			return a;
		}
	}

	/**
	 * OFFLINE OPERATION<br>
	 * Parses the data from the file obtained from mData into SScheduleDays, and
	 * stores in {@link updatedDays}. If there are no updated days saved, then
	 * {@link updatedDays} is null.
	 */
	public void parseUpdatesFile() {
		// Gets local file's updates string
		String updateS = "";
		updateS = mData.getUpdatesString();

		if (!(updateS.contains("<") || updateS.contains(">"))
				|| updateS.trim().length() == 0)
			mUpdatedDays = null;
		else {
			try {
				// MAIN PARSING OF THE SAVED FILE
				// STEP 1: Gets update time - first line of text file
				String lastUpdate = updateS.substring(0, updateS.indexOf('\n'));
				// Saves update time to SData
				mData.saveUpdateTime(lastUpdate);

				// Removes update Time from String
				updateS = updateS.substring(updateS.indexOf('\n') + 1);

				// STEP 2: Parse into individual schedule days
				// Removes the beginning < and ending >
				updateS = updateS.substring(updateS.indexOf('<') + 2,
						updateS.lastIndexOf('>') - 1);
				// Now splitting the string into various arrays by "\n>\n<\n"
				String[] updateDayStrings = updateS.split("\n>\n<\n");

				mUpdatedDays = new SScheduleDay[updateDayStrings.length];
				// Parse each day schedule into SScheduleDays
				for (int i = 0; i < updateDayStrings.length; i++) {
					mUpdatedDays[i] = new SScheduleDay(updateDayStrings[i],
							mData);
				}
			} catch (Exception e) {
				mUpdatedDays = null;
			}
		}
	}

	// Online parsing
	public boolean saveAndParseHolidaysFile() {
		String lTime = mNetwork.getHolidaysUpdateTime();
		String dTime = mData.getHolidaysUpdateTime();
		if (lTime.equals(dTime) || lTime.equals("") || lTime.equals("N/A")
				|| dTime.equals("N/A"))
			return false; // no updates
		else {
			boolean a = true;
			// Download and parse online file into file strings
			String t = mNetwork.getHolidaysFileText();
			// Saves holidays
			String[] holidayFileStrings = null;
			if (!(t.contains("<") || t.contains(">")) || t.trim().length() == 0)
				holidayFileStrings = null;
			else {
				try {
					// MAIN PARSING OF THE SAVED FILE
					// STEP 1: Gets update time - first line of text file
					String lastHolUpdate = t.substring(0, t.indexOf('\n'));
					// Saves update time to SData
					mData.saveHolidaysUpdateTime(lastHolUpdate);
					// Removes first line
					t = t.substring(t.indexOf('\n') + 1);

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
				mData.deleteAllHolidays();
				for (String s : holidayFileStrings) {
					String[] lines = s.split("\n");
					// Strings
					String start = lines[0];
					String end = lines[1].substring(lines[1].indexOf(" ") + 1);
					String name = lines[2];
					// Times
					Time startT = SStatic.getTimeFromString(start, 8);
					Time endT = SStatic.getTimeFromString(end, 8);
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
	 * OFFLINE OPERATION<br>
	 * Reads holidays from the associated SharedPreferences file.
	 * 
	 * @return whether the operation was successful
	 */
	public boolean readHolidays() {
		// Reads from SharedPreferences
		try {
			// Obtains the key set of all the holidays, i.e. all the starting
			// points of the holiday periods.
			Object[] prefDaysArray = mData.getAllHolidays().keySet().toArray();
			mHolidays = new ArrayList<SHolidayPeriod>();

			// Iterates through the array
			for (int i = 0; i < prefDaysArray.length; i++) {
				String x = (String) prefDaysArray[i];
				try {
					Integer.parseInt(x);
					if (x.length() > 8)
						mHolidays.add(x.substring(0, 8));
					else
						mHolidays.add(x);
				} catch (Exception e) {
					// Is not a holiday
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// SCHEDULE RETRIEVING
	/**
	 * Parses and returns a set of Periods, based on the current value of
	 * {@link #mToday}.
	 * 
	 * @return the timetable
	 */
	public ArrayList<SPeriod> getPeriods() {
		return mToday.getPeriods();
	}

	/**
	 * Given a string version of an updates day, parses the updates into an
	 * ArrayList of periods.
	 * 
	 * @param s
	 *            the updates day string
	 * @param mData
	 *            the SData object with saved period names
	 * @return success
	 */
	public boolean parseUpdatesString(String s, SData mData) {
		// Parses out individual period strings
		String[] lines = s.split("\n");

		// Part 1: Parses out date
		mDate = new Time();
		mDate.parse(lines[0]);
		mDate.normalize(false);

		// Part 2: Groups
		int nOfGroups = 0;
		try {
			// Start from 1; line 0 is the date
			for (int i = 1; i < lines.length; i++) {
				if (lines[i].substring(0, lines[i].indexOf(" ")).equals("GRP")) {
					// Parses out the actual group name
					String grpName = lines[i]
							.substring(lines[i].indexOf(" ") + 1);
					groups.add(grpName);
					nOfGroups++;
				} else
					break;
			}
		} catch (Exception e) {
			return false;
		}

		// Part 3: Periods
		// Loads individual strings
		for (int i = nOfGroups + 1; i < lines.length; i++) {
			if (!lines[i].substring(0, lines[i].indexOf(" ")).equals("GRP")) {
				SPeriod p = getPeriodFromString(lines[i], mData);
				periods.add(p);
			}

		}

		return true;

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
			isUpdated = UPDATED_SCHED;
			// From the array of adjusted days, gets schedule
			String sched = mUpdatedDays[adjustedIndex];
			return sched.substring(sched.indexOf('\n') + 1, sched.length());
		} else {
			isUpdated = UPDATED_NO;
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
	private SPeriod getPeriodFromString(String str) {
		SPeriod p = new SPeriod();
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

		p.mStartTime = new SPeriod.STime(sHours, sMin);
		p.mEndTime = new SPeriod.STime(eHours, eMin);

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
			if (mUpdatedDays != null) {
				// Go through adjustedDaysText to find the correct day
				for (int i = 0; i < mUpdatedDays.length; i++) {
					String s = mUpdatedDays[i];
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

	// GETS
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

	public Time getScheduleDay() {
		return scheduleDay;
	}

	/**
	 * @return whether or not the current schedule is altered or not.
	 */
	public int getIsUpdated() {
		return isUpdated;
	}

	// ADJUSTING SCHEDULE DAY
	/**
	 * Updates the local {@link scheduleDay} variable by shifting it by
	 * {@link d} days
	 * 
	 * @param d
	 *            the change in days
	 */
	public void shiftDay(int d) {
		Time t = SStatic.shiftDay(scheduleDay, d, mData);
		updateScheduleDay(t, (d >= 0));
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
		oldScheduleDay = (scheduleDay == null) ? "" : scheduleDay.toString()
				.substring(0, 8);

		// scheduleDay reflects whatever schedule is being shown
		scheduleDay = now;

		// Get day and hour based on the given Time object
		int day = scheduleDay.weekDay;
		int hour = scheduleDay.hour;

		// If the time is more than 2 hours after the end time, and it's a
		// weekday, it shifts forward one day.
		if (isForward)
			if (day > Time.SUNDAY)
				if (day < Time.SATURDAY) {
					int x = ((day == Time.WEDNESDAY) ? mData
							.getMiscDetail("endHrW") : mData
							.getMiscDetail("endHr"));

					if (x == -1) {
						x = ((day == Time.WEDNESDAY) ? 12 : 14);
					}

					if (hour >= 2 + x) {
						scheduleDay = SStatic.shiftDay(scheduleDay, 1, mData);
					}
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

	/**
	 * Parses through the local variable {@link adjustedDaysText} and returns an
	 * array of just the dates of altered schedules.
	 */
	public String[] getUpdatedDays() {
		parseUpdatesFile();
		if (mUpdatedDays != null) {
			String[] alteredDays = new String[mUpdatedDays.length];

			for (int i = 0; i < mUpdatedDays.length; i++) {
				String fullText = mUpdatedDays[i];
				alteredDays[i] = fullText.substring(0, fullText.indexOf('\n'));
			}
			return alteredDays;
		} else
			return null;
	}

	// INITIALIZATION FILES
	/**
	 * Saves the base schedule.
	 */
	public boolean downloadBaseSchedules() {
		// 1. All the base schedules
		for (int i = Time.MONDAY; i <= Time.FRIDAY; i++) {
			String schedule = mNetwork.getBaseDay(i);

			if (schedule == "" || schedule == null) {
				return false;
			} else {
				mData.saveBaseDay(i, schedule);
			}
		}
		// 2. Base group names
		mData.saveBasePeriodGroups(mNetwork.getBaseDetails());

		return true;
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
		// Files
		mData.deleteSavedUpdates();
		mData.deleteBase();
		// Prefs
		mData.deletePeriods();
		mData.deleteAllPrefs();
	}

	public void resetBase() {
		mData.saveInitialize(false);
		mData.deleteBase();
		mData.deletePeriods();
	}

	// SPINNER/GROUP STUFF
	/**
	 * Gets the spinner values for the current {@link scheduleDay}.
	 * 
	 * @return Spinner values array. null if no period groups for scheduleDay.
	 */
	public String[] getSpinnerValues() {
		String[] spinValues = null;
		if (isUpdated == UPDATED_SCHED) {
			spinValues = mData.getPeriodGroups(todayShort());
			if (spinValues != null)
				for (int i = 0; i < spinValues.length; i++) {
					spinValues[i] = SStatic.shortenCustomGrp(spinValues[i]);
				}
		} else {
			spinValues = mData.getBasePeriodGroups();
			if (spinValues != null)
				for (int i = 0; i < spinValues.length; i++) {
					spinValues[i] = spinValues[i].substring(spinValues[i]
							.indexOf(" ") + 1);
				}
		}
		return spinValues;
	}

	/**
	 * Gets the currently selected group number for the current schedule.
	 * 
	 * @return the groupN, 1-n
	 */
	public int getSelectedGroupN() {
		if (isUpdated == UPDATED_SCHED)
			return mData.getGroupPref(todayShort());
		else
			return mData.getBaseGroupPref();
	}

	/**
	 * Sets the group number for the current scheduleDay.
	 * 
	 * @param groupN
	 */
	public void groupSelected(int groupN) {
		if (isUpdated == UPDATED_SCHED) {
			String today = scheduleDay.toString().substring(0, 8);
			mData.saveGroupPref(today, groupN);
		} else if (isUpdated == UPDATED_NO) {
			mData.saveBaseGroupPref(groupN);
		}
	}

	// MISC
	private String todayShort() {
		return scheduleDay.toString().substring(0, 8);
	}

	public void setToLatestDay(boolean isForward) {
		Time t = new Time();
		String s = mData.getLatestDay();
		if (!s.equals("")) {
			try {
				t.parse(s);
				t.normalize(false);
			} catch (Exception e) {
				t = SStatic.now;
			}
		} else {
			t = SStatic.now;
		}
		updateScheduleDay(t, isForward);
	}

	// HOLIDAYS
	public boolean goToNextHoliday() {
		int index = isHolidayInFuture();
		if (index >= 0) {
			updateScheduleDay(
					SStatic.getTimeFromString(mHolidays.get(index), 8), true);
			return true;
		} else {
			return false;
		}
	}

	public int isHolidayInFuture() {
		Integer diff = null;
		int n, index = -1;
		if (mHolidays.size() > 0) {
			for (int i = 0; i < mHolidays.size(); i++) {
				n = SStatic.getJulianDay(SStatic.getTimeFromString(
						mHolidays.get(i), 8))
						- SStatic.getJulianDay(scheduleDay);
				if (diff == null) {
					if (n > 0) {
						diff = n;
						index = i;
					}
				} else {
					if (n > 0 && n < diff) {
						diff = n;
						index = i;
					}
				}

			}
		}
		return index;
	}

	public boolean getShouldUpdateSpinner() {
		if (oldIsUpdated == -1)
			return true;

		if (!oldScheduleDay.equals(scheduleDay.toString().substring(0, 8))) {
			oldScheduleDay = scheduleDay.toString().substring(0, 8);
			if (isUpdated == SParser.UPDATED_SCHED)
				return true;
			else if (oldIsUpdated == SParser.UPDATED_SCHED)
				return true;
		}

		return false;
	}
}
