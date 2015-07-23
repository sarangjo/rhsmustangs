package com.sarangjoshi.rhsmustangs.content;

import android.text.format.Time;

import java.util.List;

/**
 * Created by Sarang on 7/21/2015.
 */
public class SSchedule {
    // TODO: Decide whether a list of weeks is needed
    //private List<SWeek> mLoadedWeeks;
    private SWeek mCurrentWeek;
    private Time mToday;
    private int mGroupN;

    public SSchedule(SWeek currentWeek, Time today, int groupN) {
        mCurrentWeek = currentWeek;
        mToday = today;
        mGroupN = groupN;
    }

    public SDay getToday() {
        return mCurrentWeek.getDay(mToday.weekDay);
    }

    /**
     * Gets today's periods with the previously set group number.
     * @return
     */
    public List<SPeriod> getTodayPeriods() {
        return getToday().getPeriods(mGroupN);
    }

    /**
     * Shifts the current day of the week by a single day, forward or backward.
     *
     * @return whether the week was changed
     */
    public boolean shiftCurrentDayBy1(boolean isForward) {
        // Shift the actual date
        mToday.monthDay += (isForward) ? 1 : -1;
        // Update the other fields
        mToday.normalize(false);

        boolean weekChanged = false;
        // If there's a change in week, update the current day and week
        if (isForward && mToday.weekDay == Time.SATURDAY) {
            // shift week forward by one
            mToday.monthDay += 2;
            weekChanged = true;
        } else if (!isForward && mToday.weekDay == Time.SUNDAY) {
            // shift week back by one
            mToday.monthDay -= 2;
            weekChanged = true;
        }

        if(weekChanged) {
            mToday.normalize(false);
            // TODO: update week
        }

        return weekChanged;
    }

    public Time getTodayAsTime() {
        return mToday;
    }

    public int getGroupN() {
        return mGroupN;
    }

    public void setGroupN(int groupN) {
        this.mGroupN = groupN;
    }
}
