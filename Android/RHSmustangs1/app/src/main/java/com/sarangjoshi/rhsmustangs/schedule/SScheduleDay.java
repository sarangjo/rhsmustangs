/**
 * SSchedule.java
 * Sep 28, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import java.util.ArrayList;

import android.text.format.Time;

public class SScheduleDay extends SDay {
	private ArrayList<String> mGroups;

	public SScheduleDay(Time newDate, DayStyle d, ArrayList<SPeriod> periods,
			ArrayList<String> groups) {
		super(newDate, d);
		setPeriods(periods);
		mGroups = groups;
	}

	/**
	 * Parses and returns the entire collection of group names as a text block.
	 * This is the format in which group names are saved in the local storage.
	 */
	public String getGroupsAsText() {
		String s = "";
		for (int i = 0; i < mGroups.size(); i++) {
			s += getDate() + " " + i + " " + mGroups.get(i) + "\n";
		}
		return s;
	}

	/**
	 * Gets the groups for this day.
	 */
	public ArrayList<String> getGroups() {
		return mGroups;
	}
}
