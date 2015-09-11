package com.sarangjoshi.rhsmustangs.content;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.sarangjoshi.rhsmustangs.R;
import com.sarangjoshi.rhsmustangs.helper.SHelper;
import com.sarangjoshi.rhsmustangs.helper.ScheduleDbHelper;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a schedule object.
 *
 * @author Sarang
 */
public class SSchedule {
    public static final String GROUP_N_KEY = "group_n";
    public static final int DEFAULT_GROUP_N = 1;

    // TODO: Decide whether a list of weeks is needed
    private SWeek mCurrentWeek;
    private Calendar mToday;
    private int mGroupN;

    private SharedPreferences mSchedulePref;

    private final List<SUpdatedDay> mUpdatedDays;
    private ScheduleDbHelper mDatabase;
    private UpdateFinishedListener mListener;
    private BaseDayUpdateFinishedListener mBListener;

    private final List<SHoliday> mHolidays;

    /**
     * Constructs a new {@link SSchedule} object.
     */
    public SSchedule(Calendar today, UpdateFinishedListener l, BaseDayUpdateFinishedListener bl, Context context) {
        this.mUpdatedDays = new LinkedList<>();
        this.mHolidays = new LinkedList<>();

        this.mListener = l;
        this.mBListener = bl;

        this.mDatabase = new ScheduleDbHelper(context);
        //this.mDatabase.init();

        this.mSchedulePref = context.getSharedPreferences(context.getString(R.string.schedule_preference_file), Context.MODE_PRIVATE);

        // Sets the date
        setToday(today);
        refreshWeek(today);

        // Loads the group number
        loadGroupN();
    }

    // GETTERS

    /**
     * Gets the current day.
     */
    public SDay getToday() {
        SHoliday holiday = getHoliday();
        if (holiday != null) {
            // TODO: ???
            return holiday.getDay(mToday.get(Calendar.DAY_OF_WEEK));
        }
        return mCurrentWeek.getDay(mToday.get(Calendar.DAY_OF_WEEK));//mToday;
    }

    /**
     * Gets the holiday.
     *
     * @return null if the given day isn't in a getHoliday
     */
    public SHoliday getHoliday() {
        Calendar day = getTodayAsCalendar();
        for (SHoliday holiday : mHolidays) {
            if (holiday.contains(day)) {
                return holiday;
            }
        }
        return null;
    }

    /**
     * @return the currently selected group number.
     */
    public int getGroupN() {
        if (getToday().getClass() == SUpdatedDay.class)
            return ((SUpdatedDay) getToday()).getGroupN();
        else
            return mGroupN;
    }

    /**
     * @return today's periods with the previously set group number.
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

    public String getTodayAsString() {
        // TODO: fix
        /*Calendar now = new GregorianCalendar();
        int diff = SHelper.getAbsDay(now) - SHelper.getAbsDay(mToday); //now.compareTo(mToday);
        if (diff == 0)
            return "Today";
        else if (diff == -1)
            return "Tomorrow";
        else if (diff == 1)
            return "Yesterday";*/
        return SHelper.getDisplayString(mToday);
    }

    // SETTERS

    /**
     * Sets the current group number.
     *
     * @param groupN the group number
     * @return whether the group number was actually updated
     * @throws IllegalArgumentException if the group number is 0 and there are groups in the current
     *                                  day
     */
    public boolean setGroupN(int groupN) {
        if (getToday().hasGroups()) {
            if (groupN == SPeriod.BASE_GROUPN)
                throw new IllegalArgumentException();
            if (getToday().getClass() == SUpdatedDay.class) {
                return ((SUpdatedDay) getToday()).setGroupN(groupN);
            } else if (this.mGroupN != groupN) {
                this.mGroupN = groupN;
                return true;
            }
        }
        return false;
    }

    /**
     * Sets today for the given day.
     */
    public void setToday(Calendar today) {
        Calendar oldToday = mToday;
        mToday = today;

        // Check for Saturday/Sunday overflow
        boolean weekChanged = updateDay(true);

        // Check to see if the week has been completely changed
        if (oldToday != null)
            weekChanged |= !SHelper.sameWeek(oldToday, mToday);

        // If week changed, change the damn week
        if (weekChanged) refreshWeek(mToday);
    }

    /**
     * Shifts the current day of the week by a single day, forward or backward.
     */
    public void shiftTodayBy(int n) {
        // Shift the actual date
        Calendar newToday = new GregorianCalendar(mToday.get(Calendar.YEAR),
                mToday.get(Calendar.MONTH), mToday.get(Calendar.DAY_OF_MONTH));
        newToday.add(Calendar.DAY_OF_MONTH, n);
        mToday = newToday;

        if (updateDay(n >= 0))
            refreshWeek(mToday);
    }

    /**
     * Updates the day based on whether it is a weekend.
     *
     * @param isForward which direction the day was changed
     * @return if the week was changed
     */
    private boolean updateDay(boolean isForward) {
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

        return weekChanged;
    }

    /**
     * Goes through MONDAY to FRIDAY and sets up the current week, overriding days that have
     * updates.
     */
    public void refreshWeek(Calendar today) {
        // TODO: make more efficient by not changing week appropriately
        // Based on today, establish MONDAY
        mCurrentWeek = SWeek.getDefaultWeek();
        mCurrentWeek.update(today, mUpdatedDays);
    }

    // UPDATED DAYS

    /**
     * Adds a new {@link SUpdatedDay} in a sorted location.
     */
    public void addUpdatedDay(SUpdatedDay day) {
        synchronized (mUpdatedDays) {
            mUpdatedDays.add(day);
            Collections.sort(mUpdatedDays);
        }
    }

    /**
     * Updates the updated days list.
     */
    public void updateUpdatedDays() {
        mUpdatedDays.clear();

        /*SPeriod p = new SPeriod("01", 0, 30, 23, 59, 0);
        p.setNote("Testing note system.");
        addUpdatedDay(SUpdatedDay.test(new GregorianCalendar(2015, Calendar.SEPTEMBER, 18),
                null, p));*/

        // Queries Parse database
        ParseQuery<ParseObject> updatedDaysQuery = ParseQuery.getQuery(SUpdatedDay.UPDATED_DAY_CLASS);
        updatedDaysQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> dayObjects, ParseException e) {
                if (e == null) {
                    // Account for any locally added days (primarily testing)
                    final int localDays = mUpdatedDays.size();

                    // Retrieve periods for each day
                    for (final ParseObject obj : dayObjects) {
                        ParseRelation<ParseObject> periods = obj.getRelation(SUpdatedDay.PERIODS_KEY);
                        periods.getQuery().findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> results, ParseException e) {
                                if (e == null) {
                                    addUpdatedDay(SUpdatedDay.newFromParse(obj, results));
                                    finishedAdding(dayObjects.size() + localDays);
                                }
                                // TODO: handle
                            }
                        });
                    }
                }
                // TODO: handle
            }
        });
        //}
    }

    /**
     * Only finishes executing if the updated days' size matches the actual # of updated days.
     *
     * @param size the number of days downloaded
     */
    private void finishedAdding(int size) {
        if (mUpdatedDays.size() == size) {
            // done adding -- move on to holidays
            updateHolidays();
        }
    }

    /**
     * Gets a list of holidays.
     *
     * @return a list
     */
    public List<SHoliday> getHolidays() {
        return mHolidays;
    }

    // HOLIDAYS

    /**
     * TODO: sort
     */
    public void addHoliday(SHoliday day) {
        // TODO: sort
        synchronized (mHolidays) {
            mHolidays.add(day);
            Collections.sort(mHolidays);
        }
    }

    /**
     * Updates the holidays.
     */
    public void updateHolidays() {
        mHolidays.clear();

        ParseQuery<ParseObject> query = new ParseQuery<>(SHoliday.HOLIDAY_CLASS);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> holidays, ParseException e) {
                if (e == null) {
                    for (ParseObject holiday : holidays) {
                        addHoliday(SHoliday.newFromParse(holiday));
                    }
                    refreshWeek(mToday);
                    mListener.onUpdateFetchCompleted();
                }
                // TODO: handle
            }
        });
    }

    /**
     * @return success
     */
    public boolean goToNextHoliday() {
        for (SHoliday day : mHolidays) {
            if (mToday.compareTo(day.getStart()) < 0) {
                setToday(day.getStart());
                return true;
            }
        }
        return false;
    }

    // BASE DAYS

    public void updateBaseDays() {
        ParseQuery<ParseObject> baseDaysQuery = ParseQuery.getQuery(SDay.BASE_DAY_CLASS);
        baseDaysQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> baseDays, ParseException e) {
                if (e == null) {
                    saveBaseDaysFromParse(baseDays);
                }
                // TODO: handle
            }
        });
    }

    private void saveBaseDaysFromParse(final List<ParseObject> baseDays) {
        SDay.clearBaseDays();

        for (final ParseObject obj : baseDays) {
            //final ParseObject obj = dayObjects.get(0);
            ParseRelation<ParseObject> periods = obj.getRelation(SDay.PERIODS_KEY);
            periods.getQuery().findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> periods, ParseException e) {
                    if (e == null) {
                        SDay.addBaseDay(SDay.newFromParse(obj, periods));
                        finishedAddingBaseDays(baseDays.size());
                    }
                    // TODO: handle
                }
            });
        }
    }

    private void finishedAddingBaseDays(int size) {
        if (size == SDay.nOfBaseDays())
            mBListener.onBaseFetchCompleted();
    }

    // DATABASE

    /**
     * Saves updated days.
     */
    public void saveUpdates() {
        if (!mUpdatedDays.isEmpty())
            for (SUpdatedDay day : mUpdatedDays)
                mDatabase.saveUpdatedDay(day);

        if (!mHolidays.isEmpty())
            for (SHoliday day : mHolidays)
                mDatabase.createHoliday(day);
    }

    /**
     * @return success
     */
    public boolean clearUpdates() {
        mDatabase.deleteUpdates();
        mDatabase.init();
        return true;

    }

    /**
     * Loads updated days from the database.
     */
    public void loadUpdates() {
        // Gets rid of previously loaded days #rekt
        mUpdatedDays.clear();
        for (SUpdatedDay day : mDatabase.getUpdatedDays()) {
            addUpdatedDay(day);
        }

        mHolidays.clear();
        for (SHoliday day : mDatabase.getHolidays()) {
            addHoliday(day);
        }
    }

    /**
     * Loads the groups from the saved preferences.
     */
    private void loadGroupN() {
        int groupN = mSchedulePref.getInt(GROUP_N_KEY, -1);
        if (groupN == -1)
            mGroupN = DEFAULT_GROUP_N;
        else
            mGroupN = groupN;
    }

    /**
     * Saves the current group number.
     *
     * @return success
     */
    public boolean saveGroupN() {
        if (getToday().getClass() == SUpdatedDay.class) {
            return 1 == mDatabase.updateGroup((SUpdatedDay) getToday());
        } else {
            return mSchedulePref.edit()
                    .putInt(GROUP_N_KEY, mGroupN)
                    .commit();
        }
    }

    /**
     * Clears the base days database.
     */
    public void clearBaseDays() {
        mDatabase.deleteBaseDays();
        mDatabase.initBaseDays();
    }

    /**
     * Saves the base days.
     */
    public void saveBaseDays() {
        if (SDay.nOfBaseDays() == Calendar.FRIDAY - Calendar.MONDAY + 1)
            for (SDay day : SDay.baseDays)
                mDatabase.saveBaseDay(day);
    }

    public void loadBaseDays() {
        for (SDay day : mDatabase.getBaseDays()) {
            SDay.addBaseDay(day);
        }
    }

    // MISC

    /**
     * Listener for when updates finish.
     */
    public interface UpdateFinishedListener {
        void onUpdateFetchCompleted();
    }

    /**
     * Listener for when base day updates finish.
     */
    public interface BaseDayUpdateFinishedListener {
        void onBaseFetchCompleted();
    }
}
