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

    private List<SHoliday> mHolidays;

    /**
     * Constructs a new {@link SSchedule} object.
     *
     */
    public SSchedule(Calendar today, UpdateFinishedListener l, Context context) {
        this.mUpdatedDays = new LinkedList<>();
        this.mHolidays = new LinkedList<>();

        this.mListener = l;
        this.mDatabase = new ScheduleDbHelper(context);
        this.mSchedulePref = context.getSharedPreferences(
                context.getString(R.string.schedule_preference_file), Context.MODE_PRIVATE);

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
        if(holiday != null) {
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
        for(SHoliday holiday : mHolidays) {
            if(holiday.contains(day)) {
                return holiday;
            }
        }
        return null;
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
     * @return
     */
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
     * @throws IllegalArgumentException if the group number is 0 and there are groups in the current
     *                                  day
     * @returns whether the group number was actually updated
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
     *
     * @param today
     * @return if the week changed
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
     *
     * @return whether the week was changed
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
     *
     * @param day
     */
    public void addUpdatedDay(SUpdatedDay day) {
        synchronized (mUpdatedDays) {
            int i;
            for (i = 0; i < mUpdatedDays.size(); i++) {
                int d = day.compareTo(mUpdatedDays.get(i));
                if (d < 0) {
                    break;
                }
            }
            mUpdatedDays.add(i/*(i - 1 < 0) ? 0 : i - 1*/, day);
        }
    }

    /**
     * Updates the updated days list.
     */
    public void updateUpdatedDays() {
        mUpdatedDays.clear();

        boolean network = true;

        if (!network) {
            addUpdatedDay(SUpdatedDay.test(new GregorianCalendar(2015, Calendar.AUGUST, 5),
                    new String[]{"Seniors", "Juniors", "Other Lowly Beings"},
                    new SPeriod("02", 9, 30, 18, 30, 3),
                    new SPeriod("01", 7, 30, 8, 30, 0)));
            addUpdatedDay(SUpdatedDay.testPeriodSorting());
            addUpdatedDay(SUpdatedDay.test(new GregorianCalendar(2015, Calendar.AUGUST, 18),
                    null, new SPeriod("01", 0, 30, 23, 59, 0)));

            // Make sure this is how many days there are in the test run
            finishedAdding(3);
        } else {
            ParseQuery<ParseObject> updatedDaysQuery = ParseQuery.getQuery(SUpdatedDay.UPDATED_DAY_CLASS);
            updatedDaysQuery.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> updatedDays, ParseException e) {
                    if (e == null) {
                        setUpdatedDaysFromParse(updatedDays, mUpdatedDays.size());
                    } else {
                        // TODO: handle
                    }
                }
            });
        }
    }

    /**
     * Once the updated days have been downloaded, saves them.
     *
     * @param dayObjects saved ParseObjects
     * @param localDays  the number of locally saved days
     */
    private void setUpdatedDaysFromParse(final List<ParseObject> dayObjects, final int localDays) {
        for (final ParseObject obj : dayObjects) {
            //final ParseObject obj = dayObjects.get(0);
            ParseRelation<ParseObject> periods = obj.getRelation(SUpdatedDay.PERIODS_KEY);
            periods.getQuery().findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> results, ParseException e) {
                    if (e == null) {
                        addUpdatedDay(SUpdatedDay.newFromParse(obj, results));
                        finishedAdding(dayObjects.size() + localDays);
                    } else {
                        // TODO: handle
                    }
                }
            });
        }
    }

    /**
     * Only finishes executing if the updated days' size matches the actual # of updated days.
     *
     * @param size
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
        synchronized (mHolidays) {
            mHolidays.add(day);
        }
    }

    /**
     * Updates the holidays.
     */
    public void updateHolidays() {
        mHolidays.clear();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(SHoliday.HOLIDAY_CLASS);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> holidays, ParseException e) {
                if(e == null) {
                    for(ParseObject holiday : holidays) {
                        addHoliday(SHoliday.newFromParse(holiday));
                    }
                    refreshWeek(mToday);
                    mListener.updateCompleted();
                } else {
                    // TODO: handle
                }
            }
        });
    }

    // DATABASE

    /**
     * Saves updated days.
     */
    public void saveUpdatedDays() {
        if (!mUpdatedDays.isEmpty()) {
            for (SUpdatedDay day : mUpdatedDays) {
                mDatabase.saveUpdatedDay(day);
            }
            mDatabase.close();
        }
    }

    /**
     * @return success
     */
    public boolean clearDatabase() {
        mDatabase.deleteAll();
        mDatabase.init();
        return true;

    }

    /**
     * Loads updated days from the database.
     */
    public void loadDataFromDatabase() {
        // Gets rid of previously loaded days #rekt
        mUpdatedDays.clear();
        for (SUpdatedDay day : mDatabase.getUpdatedDays()) {
            addUpdatedDay(day);
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

    // MISC

    /**
     * Listener for when updates finish.
     */
    public interface UpdateFinishedListener {
        void updateCompleted();
    }

}
