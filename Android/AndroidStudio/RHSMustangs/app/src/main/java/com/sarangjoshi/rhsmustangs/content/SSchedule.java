package com.sarangjoshi.rhsmustangs.content;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.sarangjoshi.rhsmustangs.schedule.SStatic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Sarang on 7/21/2015.
 */
public class SSchedule {
    // TODO: Decide whether a list of weeks is needed
    //private List<SWeek> mLoadedWeeks;
    private SWeek mCurrentWeek;
    private Calendar mToday;
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
        SDay today = null;/*
        for(SUpdatedDay day : mUpdatedDays) {
            if(day.isToday(mToday))
                today = day;
        }*/
        
        if(today == null)
            today = mCurrentWeek.getDay(mToday.get(Calendar.DAY_OF_WEEK));
        return today;
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
    public boolean shiftCurrentDayBy1(boolean isForward) {
        // Shift the actual date
        mToday.add(Calendar.DAY_OF_MONTH, (isForward) ? 1 : -1);
        // Update the other fields
        //mToday.normalize(false);

        return dayChanged(isForward);
    }

    private boolean dayChanged(boolean isForward) {
        boolean weekChanged = false;
        // If there's a change in week, update the current day and week
        if (isForward && mToday.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            // shift week forward by one
            mToday.add(Calendar.DAY_OF_MONTH, 2);
            weekChanged = true;
        } else if (!isForward && mToday.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            // shift week back by one
            mToday.add(Calendar.DAY_OF_MONTH, -2);
            weekChanged = true;
        }

        if (weekChanged) {
            //mToday.normalize(false);
            // TODO: update week
        }
        return weekChanged;
    }

    public Calendar getTodayAsTime() {
        return mToday;
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
     * Updates the updated days list.
     */
    public void updateUpdatedDays() {
        mUpdatedDays.clear();
        mUpdatedDays.add(SUpdatedDay.test());
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
