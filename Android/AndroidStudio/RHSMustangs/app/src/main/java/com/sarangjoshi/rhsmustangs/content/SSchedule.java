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
 * Represents a schedule object.
 *
 * @author Sarang
 */
public class SSchedule {
    // TODO: Decide whether a list of weeks is needed
    //private List<SWeek> mLoadedWeeks;
    private SWeek mCurrentWeek;
    private Calendar mToday;
    private int mGroupN;

    private List<SUpdatedDay> mUpdatedDays;
    private UpdateFinishedListener finishedListener;

    /**
     * Constructs a new {@link SSchedule} object.
     *
     * @param today
     * @param groupN
     * @param l
     */
    public SSchedule(Calendar today, int groupN, UpdateFinishedListener l) {
        mGroupN = groupN;
        mUpdatedDays = new ArrayList<>();
        this.finishedListener = l;

        // Then, set the current day within that week.
        setToday(today);

        // First, update the week to reflect the current day.
        updateWeek(today);
    }

    /**
     * Gets the current day.
     */
    public SDay getToday() {
        return mCurrentWeek.getDay(mToday.get(Calendar.DAY_OF_WEEK));//mToday;
    }

    /**
     * Sets the current group number.
     *
     * @param groupN the group number
     * @throws IllegalArgumentException if the group number is 0 and there are groups in the current
     *                                  day
     * @returns whether the group number was actually updated
     */
    public boolean setGroupN(int groupN) {
        if (getToday().hasGroups()) {
            if (groupN == SPeriod.BASE_GROUPN)
                throw new IllegalArgumentException();
            if(getToday().getClass() == SUpdatedDay.class) {
                return ((SUpdatedDay) getToday()).setGroupN(groupN);
            } else if (this.mGroupN != groupN) {
                this.mGroupN = groupN;
                return true;
            }
        }
        return false;
    }

    /**
     * Gets today's periods with the previously set group number.
     *
     * @return
     */
    public List<SPeriod> getTodayPeriods() {
        return getToday().getPeriods(getGroupN());
    }

    /**
     * Gets today as a {@link Calendar} object.
     *
     * @return the current day
     */
    public Calendar getTodayAsCalendar() {
        return mToday;
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
        if (getToday().getClass() == SUpdatedDay.class)
            return ((SUpdatedDay) getToday()).getGroupN();
        else
            return mGroupN;
    }

    /**
     * @return
     */
    public String getTodayAsString() {
        /*Calendar now = new GregorianCalendar();
        int diff = SStatic.getAbsDay(now) - SStatic.getAbsDay(mToday); //now.compareTo(mToday);
        if (diff == 0)
            return "Today";
        else if (diff == -1)
            return "Tomorrow";
        else if (diff == 1)
            return "Yesterday";*/
        return SStatic.getDisplayString(mToday);
    }

    /**
     * Gets a String representation of today's day of the week.
     *
     * @return String representation of today's day of the week
     */
    public String getTodayDayOfWeekAsString() {
        return getToday().getDayOfWeekAsString();
    }

    /**
     * Sets today for the given day.
     *
     * @param today
     * @return if the week changed
     */
    public boolean setToday(Calendar today) {
        mToday = today;
        return dayChanged(true);
    }

    /**
     * Goes through MONDAY to FRIDAY and sets up the current week, overriding days that have
     * updates.
     */
    private void updateWeek(Calendar today) {
        // Based on today, establish MONDAY
        mCurrentWeek = SWeek.getDefaultWeek();
        mCurrentWeek.update(today, mUpdatedDays);
    }

    /**
     * Shifts the current day of the week by a single day, forward or backward.
     *
     * @return whether the week was changed
     */
    public boolean shiftTodayBy(int n) {
        // Shift the actual date
        mToday.add(Calendar.DAY_OF_MONTH, n);
        // Update the other fields
        //mToday.normalize(false);

        return dayChanged(n >= 0);
    }

    /**
     * ALWAYS call this when mToday is changed.
     *
     * @param isForward which direction the day was changed
     * @return if the week was changed
     */
    private boolean dayChanged(boolean isForward) {
        // Week changing algorithm
        boolean weekChanged = false;
        // If there's a change in week, update the current day and week
        if (isForward) {
            if (mToday.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                mToday.add(Calendar.DAY_OF_MONTH, 2);
                weekChanged = true;
            } else if (mToday.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                mToday.add(Calendar.DAY_OF_MONTH, 1);
                weekChanged = true;
            }
        } else {
            if (mToday.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                mToday.add(Calendar.DAY_OF_MONTH, -2);
                weekChanged = true;
            }
        }

        if (weekChanged) {
            updateWeek(mToday);
        }

        return weekChanged;
    }

    /**
     * Updates the updated days list.
     */
    public void updateUpdatedDays() {
        mUpdatedDays.clear();

        mUpdatedDays.add(SUpdatedDay.test());
        mUpdatedDays.add(SUpdatedDay.test2());

        updateWeek(mToday);

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

    /**
     * Listener for when updates finish.
     */
    public interface UpdateFinishedListener {
        void updateCompleted();
    }
}
