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
    private SDay mToday;
    private int mGroupN;

    private List<SUpdatedDay> mUpdatedDays;
    private UpdateFinishedListener finishedListener;

    /**
     * Constructs a new {@link SSchedule} object.
     * @param today
     * @param groupN
     * @param l
     */
    public SSchedule(Calendar today, int groupN, UpdateFinishedListener l) {
        mGroupN = groupN;
        mUpdatedDays = new ArrayList<>();
        this.finishedListener = l;

        setToday(today);
        updateWeek();
    }

    /**
     * Gets the current day.
     */
    public SDay getToday() {
        return mToday;
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
     * Gets today as a {@link Calendar} object.
     *
     * @return the current day
     */
    public Calendar getTodayAsTime() {
        return mTodayTime;
    }

    /**
     * Gets a list of updated days.
     *
     * @return a list of updated days
     */
    public List<SUpdatedDay> getUpdatedDays() {
        return mUpdatedDays;
    }

    /**
     * Gets the currently selected group number.
     *
     * @return
     */
    public int getGroupN() {
        return mGroupN;
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

    public String getTodayDayOfWeekAsString() {
        return getToday().getDayOfWeekAsString();
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
     * Goes through MONDAY to FRIDAY and sets up the current week, overriding days that have
     * updates.
     */
    private void updateWeek() {
        // Based on today, establish MONDAY
        mCurrentWeek = SWeek.getDefaultWeek();
        mCurrentWeek.update(mTodayTime, mUpdatedDays);
    }

    /**
     * Shifts the current day of the week by a single day, forward or backward.
     *
     * @return whether the week was changed
     */
    public boolean shiftTodayBy(int n, boolean isForward) {
        // Shift the actual date
        mTodayTime.add(Calendar.DAY_OF_MONTH, (isForward) ? n : -n);
        // Update the other fields
        //mTodayTime.normalize(false);

        return dayChanged(isForward);
    }

    /**
     * ALWAYS call this when mTodayTime is changed.
     *
     * @param isForward which direction the day was changed
     * @return if the week was changed
     */
    private boolean dayChanged(boolean isForward) {
        // Local SDay reset
        //mToday = null;

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
            updateWeek();
        }

        // Update mToday
        mToday = mCurrentWeek.getDay(mTodayTime.get(Calendar.DAY_OF_WEEK));

        return weekChanged;
    }

    /**
     * Updates the updated days list.
     */
    public void updateUpdatedDays() {
        mUpdatedDays.clear();

        mUpdatedDays.add(SUpdatedDay.test());
        mUpdatedDays.add(SUpdatedDay.test2());

        updateWeek();

        finishedListener.updateCompleted();

        /*
        ParseQuery<ParseObject> updatedDaysQuery = ParseQuery.getQuery(SUpdatedDay.UPDATED_DAY_CLASS);
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

    public interface UpdateFinishedListener {
        void updateCompleted();
    }
}
