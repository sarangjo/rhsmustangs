package com.sarangjoshi.rhsmustangs.content;

import android.content.SharedPreferences;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.sarangjoshi.rhsmustangs.helper.SHelper;
import com.sarangjoshi.rhsmustangs.helper.ScheduleDbHelper;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * TODO: add class comment
 *
 * @author Sarang Joshi
 */
public class Updates {
    public static final String LATEST_UPDATE_KEY = "latest-update";
    private static final String LATEST_HOL_UPDATE_KEY = "latest-hol-update";

    //private final List<UpdatedDay> mUpdatedDays;
    private final Map<Integer, UpdatedDay> mUpdatedDays;
    private final List<Holiday> mHolidays;

    private UpdatesListener mListener;
    private SharedPreferences mSharedPref;

    public Updates(UpdatesListener l, SharedPreferences pref) {
        mUpdatedDays = new HashMap<>();
        mHolidays = new LinkedList<>();
        mListener = l;
        mSharedPref = pref;
    }

    // UPDATED DAYS

    // TODO: phase out
    public List<UpdatedDay> getUpdatedDaysList() {
        List<UpdatedDay> updatedDays = new LinkedList<>();
        for (int day : mUpdatedDays.keySet()) {
            updatedDays.add(mUpdatedDays.get(day));
        }
        return updatedDays;
    }

    public Map<Integer, UpdatedDay> getUpdatedDays() {
        return mUpdatedDays;
    }

    /**
     * Adds a new {@link UpdatedDay} in a sorted location.
     */
    public void addUpdatedDay(UpdatedDay day) {
        synchronized (mUpdatedDays) {
            int jDay = SHelper.getJulianDay(day.getDate());
            mUpdatedDays.put(jDay, day);
        }
    }

    /**
     * Updates the updated days list.
     */
    public void updateUpdatedDays() {
        // Queries Parse database
        ParseQuery<ParseObject> updatedDaysQuery = ParseQuery.getQuery(UpdatedDay.UPDATED_DAY_CLASS);
        updatedDaysQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> dayObjects, ParseException e) {
                if (e == null) {
                    if (areObjectsUpdated(mSharedPref.getString(LATEST_UPDATE_KEY, ""), dayObjects)) {
                        // Clear out old updated days
                        mUpdatedDays.clear();
                        for (final ParseObject obj : dayObjects) {
                            ParseRelation<ParseObject> periods = obj.getRelation(UpdatedDay.PERIODS_KEY);
                            periods.getQuery().findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> results, ParseException e) {
                                    if (e == null) {
                                        addUpdatedDay(UpdatedDay.newFromParse(obj, results));
                                        finishedAdding(dayObjects.size(), true);
                                    }
                                    // TODO: handle
                                }
                            });
                        }
                        // Save latest update
                        saveLatestUpdate(LATEST_UPDATE_KEY, dayObjects);
                    } else {
                        finishedAdding(mUpdatedDays.size(), false);
                    }
                }
                // TODO: handle
            }
        });
        //}
    }

    /**
     * @param dayObjects
     */
    private void saveLatestUpdate(String key, List<ParseObject> dayObjects) {
        if (dayObjects.size() > 0) {
            Date latestDate = dayObjects.get(0).getUpdatedAt();
            for (int i = 1; i < dayObjects.size(); i++) {
                if (dayObjects.get(i).getUpdatedAt().compareTo(latestDate) >= 0) {
                    latestDate = dayObjects.get(i).getUpdatedAt();
                }
            }
            mSharedPref.edit().putString(key, SHelper.dateTimeToString(latestDate))
                    .apply();
        }
    }

    /**
     * Only finishes executing if the updated days' size matches the actual # of updated days.
     *
     * @param size the number of days downloaded
     */
    private void finishedAdding(int size, boolean updated) {
        if (!updated || mUpdatedDays.size() == size) {
            // done adding -- move on to holidays
            updateHolidays(updated);
        }
    }

    // HOLIDAYS

    /**
     * Gets a list of holidays.
     *
     * @return a list
     */
    public List<Holiday> getHolidays() {
        return mHolidays;
    }

    /**
     * TODO: sort
     */
    public void addHoliday(Holiday day) {
        // TODO: sort
        synchronized (mHolidays) {
            mHolidays.add(day);
            Collections.sort(mHolidays);
        }
    }

    /**
     * Updates the holidays.
     */
    public void updateHolidays(final boolean updated) {
        ParseQuery<ParseObject> query = new ParseQuery<>(Holiday.HOLIDAY_CLASS);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> holidays, ParseException e) {
                if (e == null) {
                    // Checks if the holidays are in fact updated
                    if (areObjectsUpdated(mSharedPref.getString(LATEST_HOL_UPDATE_KEY, ""), holidays)) {
                        // Resaves holidays
                        mHolidays.clear();
                        for (ParseObject holiday : holidays) {
                            addHoliday(Holiday.newFromParse(holiday));
                        }
                        saveLatestUpdate(LATEST_HOL_UPDATE_KEY, holidays);
                        mListener.updatesCompleted(updated, true);
                    } else {
                        mListener.updatesCompleted(updated, false);
                    }
                }
                // TODO: handle
            }
        });
    }

    private boolean areObjectsUpdated(String latestUpdate, List<ParseObject> objects) {
        if (!latestUpdate.isEmpty()) {
            Calendar latestUpdateCal = SHelper.stringToDateTimeCalendar(latestUpdate);
            for (ParseObject obj : objects) {
                Calendar update = SHelper.dateToCalendar(obj.getUpdatedAt());
                int diff = update.compareTo(latestUpdateCal);
                if (diff > 0) {
                    // This day was updated later than saved
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }

    }

    public void saveUpdatedDays(ScheduleDbHelper db) {
        if (!mUpdatedDays.isEmpty())
            for (int day : mUpdatedDays.keySet())
                db.saveUpdatedDay(mUpdatedDays.get(day));
    }

    public void saveHolidays(ScheduleDbHelper db) {
        if (!mHolidays.isEmpty())
            for (Holiday day : mHolidays)
                db.createHoliday(day);
    }

    public void loadUpdatedDays(ScheduleDbHelper db) {
        mUpdatedDays.clear();
        for (UpdatedDay day : db.getUpdatedDays()) {
            addUpdatedDay(day);
        }
    }

    public void loadHolidays(ScheduleDbHelper db) {
        mHolidays.clear();
        for (Holiday day : db.getHolidays()) {
            addHoliday(day);
        }
    }

    public interface UpdatesListener {
        public void updatesCompleted(boolean updatedDays, boolean holidays);
    }
}
