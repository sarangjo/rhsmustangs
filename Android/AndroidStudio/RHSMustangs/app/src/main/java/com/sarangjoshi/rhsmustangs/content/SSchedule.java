package com.sarangjoshi.rhsmustangs.content;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.sarangjoshi.rhsmustangs.schedule.SStatic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sarang on 7/21/2015.
 */
public class SSchedule {
    // TODO: Decide whether a list of weeks is needed
    //private List<SWeek> mLoadedWeeks;
    private SWeek mCurrentWeek;
    private Calendar mTodayTime;
    /**
     * Set to null if mTodayTime changes.
     */
    private SDay mToday;
    private int mGroupN;

    private List<SUpdatedDay> mUpdatedDays;
    private UpdateFinishedListener finishedListener;

    public SSchedule(SWeek currentWeek, Calendar today, int groupN, UpdateFinishedListener l) {
        mCurrentWeek = currentWeek;
        setToday(today);
        mGroupN = groupN;
        mUpdatedDays = new ArrayList<>();
        this.finishedListener = l;
    }

    /**
     * Gets the current day.
     */
    public SDay getToday() {
        return ((mToday == null) ? retrieveToday() : mToday);
    }

    /**
     * Retrieves today if the day has changed.
     * @return
     */
    private SDay retrieveToday() {
        SDay today = null;
        for(SUpdatedDay day : mUpdatedDays) {
            if(day.isToday(mTodayTime))
                today = day;
        }

        if(today == null)
            today = mCurrentWeek.getDay(mTodayTime.get(Calendar.DAY_OF_WEEK));
        return today;
    }

    /**
     * Sets today for the given day.
     *
     * @param today
     * @return if the week changed
     */
    public boolean setToday(Calendar today) {
        mTodayTime = today;
        return dayChanged(true);
    }

    /**
     * Gets today's periods with the previously set group number.
     *
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
    public boolean shiftCurrentDayBy(int n, boolean isForward) {
        // Shift the actual date
        mTodayTime.add(Calendar.DAY_OF_MONTH, (isForward) ? n : -n);
        // Update the other fields
        //mTodayTime.normalize(false);

        return dayChanged(isForward);
    }

    /**
     * Call this when mTodayTime is changed.
     *
     * @param isForward which direction the day was changed
     * @return if the week was changed
     */
    private boolean dayChanged(boolean isForward) {
        // Local SDay reset
        mToday = null;

        // Week changing algorithm
        boolean weekChanged = false;
        // If there's a change in week, update the current day and week
        if (isForward && mTodayTime.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            // shift week forward by one
            mTodayTime.add(Calendar.DAY_OF_MONTH, 2);
            weekChanged = true;
        } else if (!isForward && mTodayTime.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            // shift week back by one
            mTodayTime.add(Calendar.DAY_OF_MONTH, -2);
            weekChanged = true;
        }

        if (weekChanged) {
            //mTodayTime.normalize(false);
            // TODO: update week
        }
        return weekChanged;
    }

    public Calendar getTodayAsTime() {
        return mTodayTime;
    }

    public int getGroupN() {
        return mGroupN;
    }

    /**
     * Sets the current group number
     *
     * @param groupN the group number
     * @throws IllegalArgumentException if the group number is 0 and there are groups in the current
     *                                  day
     * @returns whether the group number was actually updated
     */
    public boolean setGroupN(int groupN) {
        if (getToday().hasGroups()) {
            if (groupN == 0)
                throw new IllegalArgumentException();
            if (this.mGroupN != groupN) {
                this.mGroupN = groupN;
                return true;
            }
        }
        return false;
    }

    /**
     * @return
     */
    public String getTodayAsString() {
        /*Calendar now = new GregorianCalendar();
        int diff = SStatic.getAbsDay(now) - SStatic.getAbsDay(mTodayTime); //now.compareTo(mTodayTime);
        if (diff == 0)
            return "Today";
        else if (diff == -1)
            return "Tomorrow";
        else if (diff == 1)
            return "Yesterday";*/
        return SStatic.getDisplayString(mTodayTime);
    }

    /**
     * Updates the updated days list.
     */
    public void updateUpdatedDays() {
        mUpdatedDays.clear();

        mUpdatedDays.add(SUpdatedDay.test());
        mUpdatedDays.add(SUpdatedDay.test2());

        finishedListener.updateCompleted();

        /*
        ParseQuery<ParseObject> updatedDaysQuery = ParseQuery.getQuery("UpdatedDay");
        updatedDaysQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> updatedDays, ParseException e) {
                if(e == null) {
                    setUpdatedDaysFromParse(updatedDays);
                    finishedListener.refreshCompleted();
                } else {
                    // TODO: handle
                }
            }
        });*/
    }

    public List<SUpdatedDay> getUpdatedDays() {
        return mUpdatedDays;
    }

    private void setUpdatedDaysFromParse(List<ParseObject> dayObjects) {
        mUpdatedDays.clear();
        //for(ParseObject obj : dayObjects) {
        final ParseObject obj = dayObjects.get(0);
        ParseRelation<ParseObject> periods = obj.getRelation(SUpdatedDay.PERIODS_KEY);
        periods.getQuery().findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> results, ParseException e) {
                if (e == null) {
                    mUpdatedDays.add(SUpdatedDay.newFromParse(obj, results));
                } else {
                    // TODO: handle
                }
            }
        });
        //}
    }

    public String getTodayDayOfWeekAsString() {
        return getToday().getDayOfWeekAsString();
    }

    public interface UpdateFinishedListener {
        void updateCompleted();
    }
}
