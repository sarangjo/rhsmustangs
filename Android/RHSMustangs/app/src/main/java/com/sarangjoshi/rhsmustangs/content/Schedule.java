package com.sarangjoshi.rhsmustangs.content;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.sarangjoshi.rhsmustangs.R;
import com.sarangjoshi.rhsmustangs.helper.SHelper;
import com.sarangjoshi.rhsmustangs.helper.ScheduleDbHelper;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Represents a schedule object.
 *
 * @author Sarang
 */
public class Schedule implements Updates.UpdatesListener {
    public static final String GROUP_N_KEY = "group_n";

    // TODO: Decide whether a list of weeks is needed
    private Week mCurrentWeek;
    private Calendar mToday;
    private int mGroupN;

    private SharedPreferences mSchedulePref;

    private Updates mUpdates;
    private ScheduleDbHelper mDatabase;
    private UpdateFinishedListener mListener;
    private BaseDayUpdateFinishedListener mBListener;

    /**
     * Constructs a new {@link Schedule} object.
     */
    public Schedule(Calendar today, UpdateFinishedListener l, BaseDayUpdateFinishedListener bl, Context context) {
        this.mListener = l;
        this.mBListener = bl;

        this.mDatabase = new ScheduleDbHelper(context);
        this.mSchedulePref = context.getSharedPreferences(context.getString(R.string.schedule_preference_file), Context.MODE_PRIVATE);
        this.mUpdates = new Updates(this, mSchedulePref);

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
    public Day getToday() {
        Day d = getDay(mToday);
        return d;

        /*
        Holiday holiday = getHoliday(getTodayAsCalendar());
        if (holiday != null) {
            return holiday.getDay(getTodayAsCalendar().get(Calendar.DAY_OF_WEEK));
        }
        return mCurrentWeek.getDay(getTodayAsCalendar().get(Calendar.DAY_OF_WEEK));//mToday;*/
    }

    /**
     * Gets the holiday.
     *
     * @return null if the given day isn't in a getHoliday
     */
    public Holiday getHoliday(Calendar day) {
        for (Holiday holiday : mUpdates.getHolidays()) {
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
        if (getToday().getClass() == UpdatedDay.class)
            return ((UpdatedDay) getToday()).getGroupN();
        else
            return mGroupN;
    }

    /**
     * @return today's periods with the previously set group number.
     */
    public List<Period> getTodayPeriods() {
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
    public Updates getUpdates() {
        return mUpdates;
    }

    public String getTodayAsString() {
        // TODO: fix
        /*Calendar now = new GregorianCalendar();
        int diff = SHelper.getJulianDay(now) - SHelper.getJulianDay(mToday); //now.compareTo(mToday);
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
            if (groupN == Period.BASE_GROUPN)
                throw new IllegalArgumentException();
            if (getToday().getClass() == UpdatedDay.class) {
                return ((UpdatedDay) getToday()).setGroupN(groupN);
            } else if (this.mGroupN != groupN) {
                this.mGroupN = groupN;
                return true;
            }
        }
        return false;
    }

    /**
     * Sets today for the given day.
     *
     * @returns the number of days changed
     */
    public int setToday(Calendar today) {
        int diff = SHelper.compareAbsDays(mToday, today);
        mToday = today;

        return diff;
        /*
        // Check for Saturday/Sunday overflow
        boolean weekChanged = updateDay(diff >= 0);

        // Check to see if the week has been completely changed
        if (oldToday != null)
            weekChanged |= !SHelper.sameWeek(oldToday, mToday);

        // If week changed, change the damn week
        if (weekChanged) refreshWeek(mToday);*/
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
        mCurrentWeek = Week.getDefaultWeek();
        mCurrentWeek.update(today, mUpdates.getUpdatedDaysList());
    }

    // HOLIDAYS

    /**
     * @return success
     */
    public boolean goToNextHoliday() {
        for (Holiday day : mUpdates.getHolidays()) {
            if (mToday.compareTo(day.getStart()) < 0) {
                setToday(day.getStart());
                return true;
            }
        }
        return false;
    }

    // BASE DAYS

    public void updateBaseDays() {
        ParseQuery<ParseObject> baseDaysQuery = ParseQuery.getQuery(Day.BASE_DAY_CLASS);
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
        Day.clearBaseDays();

        for (final ParseObject obj : baseDays) {
            //final ParseObject obj = dayObjects.get(0);
            ParseRelation<ParseObject> periods = obj.getRelation(Day.PERIODS_KEY);
            periods.getQuery().findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> periods, ParseException e) {
                    if (e == null) {
                        Day.addBaseDay(Day.newFromParse(obj, periods));
                        finishedAddingBaseDays(baseDays.size());
                    }
                    // TODO: handle
                }
            });
        }
    }

    private void finishedAddingBaseDays(int size) {
        if (size == Day.nOfBaseDays())
            mBListener.onBaseFetchCompleted();
    }

    // DATABASE

    /**
     * Saves updated days.
     */
    public void saveUpdates() {
        mUpdates.saveUpdatedDays(mDatabase);
        mUpdates.saveHolidays(mDatabase);
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
        mUpdates.loadUpdatedDays(mDatabase);
        mUpdates.loadHolidays(mDatabase);
    }

    /**
     * Loads the groups from the saved preferences.
     */
    private void loadGroupN() {
        int groupN = mSchedulePref.getInt(GROUP_N_KEY, -1);
        if (groupN == -1)
            mGroupN = Period.DEFAULT_GROUP_N;
        else
            mGroupN = groupN;
    }

    /**
     * Saves the current group number.
     *
     * @return success
     */
    public boolean saveGroupN() {
        if (getToday().getClass() == UpdatedDay.class) {
            return 1 == mDatabase.updateGroup((UpdatedDay) getToday());
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
        if (Day.nOfBaseDays() == Calendar.FRIDAY - Calendar.MONDAY + 1)
            for (Day day : Day.baseDays)
                mDatabase.saveBaseDay(day);
    }

    public void loadBaseDays() {
        for (Day day : mDatabase.getBaseDays()) {
            Day.addBaseDay(day);
        }
    }

    @Override
    public void updatesCompleted(boolean updatedDays, boolean holidays) {
        if (updatedDays || holidays)
            refreshWeek(mToday);
        mListener.onUpdateFetchCompleted(updatedDays, holidays);
    }

    /**
     * Given a date, gets the appropriate Day object.
     */
    public Day getDay(Calendar date) {
        // Validity
        if (date.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                || date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            return null;
        // Holiday
        Holiday holiday = getHoliday(date);
        if (holiday != null)
            return holiday.getDay(date.get(Calendar.DAY_OF_WEEK));
        // UpdatedDay
        UpdatedDay uDay = mUpdates.getUpdatedDays().get(SHelper.getJulianDay(date));
        if (uDay != null)
            return uDay;
        // Base
        return Day.getBaseDay(date.get(Calendar.DAY_OF_WEEK));
    }

    public int getGroup(Day day) {
        if (day != null) {
            if (day.getClass() == UpdatedDay.class)
                return ((UpdatedDay) day).getGroupN();
            else
                return mGroupN;
        }
        return Period.DEFAULT_GROUP_N;
    }

    // MISC

    /**
     * Listener for when updates finish.
     */
    public interface UpdateFinishedListener {
        void onUpdateFetchCompleted(boolean updatedDays, boolean holidays);
    }

    /**
     * Listener for when base day updates finish.
     */
    public interface BaseDayUpdateFinishedListener {
        void onBaseFetchCompleted();
    }
}
