/**
 * SParser3.java
 * Oct 10, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import java.util.ArrayList;

import android.content.Context;
import android.text.format.Time;

public class SParser3 {
	/**
	 * The Activity tied to this parser.
	 */
	private Context mContext;

	/**
	 * A variable showing the "old" today
	 */
	private Time mOldToday;
	
	/**
	 * The current day being displayed.
	 */
	private SDay mToday;
	/**
	 * The day directly to the left of the active screen.
	 */
	private SDay mYesterday;
	/**
	 * The day directly to the right of the active screen.
	 */
	private SDay mTomorrow;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            attached Activity
	 */
	public SParser3(Context context) {
		mContext = context;
	}

	/**
	 * Gets {@link #mToday}'s periods.
	 */
	public ArrayList<SPeriod> getPeriods() {
		return mToday.getPeriods();
	}

	/**
	 * Updates {@link #mToday} to a the new date.
	 * 
	 * @param newDate
	 * @param isForward
	 */
	public void updateToday(Time newDate, boolean isForward) {
		
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
}
