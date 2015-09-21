package com.sarangjoshi.rhsmustangs.content;

import android.provider.SyncStateContract;

import com.parse.ParseObject;
import com.sarangjoshi.rhsmustangs.helper.SHelper;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class Day {
    public static final String BASE_DAY_CLASS = "BaseDay";
    public static final String PERIODS_KEY = "periods";
    private static final String GROUPS_KEY = "groupNames";
    private static final String DAY_OF_WEEK_KEY = "dayOfWeek";

    protected final List<Period> mPeriods;
    private int mDayOfWeek;
    private String[] mGroupNames;

    /**
     * For optimization; hidden from client
     */
    private List<Period> mTruncatedPeriods;
    private int currentGroupN = Period.BASE_GROUPN;

    /**
     * Creates a new {@link Day} with the given day of the week and given group names.
     *
     * @param dayOfWeek  day of the week
     * @param groupNames null means there are no group names.
     */
    public Day(int dayOfWeek, String[] groupNames) {
        this.mDayOfWeek = dayOfWeek;
        this.mPeriods = new ArrayList<>();
        this.mGroupNames = groupNames;
    }

    /**
     * Creates a new {@link Day} with the given day of the week and no groups.
     */
    public Day(int dayOfWeek) {
        this(dayOfWeek, null);
    }

    /**
     * Adds a period to the day.
     *
     * @param period {@link Period} to be added
     */
    public void addPeriod(Period period) {
        synchronized (mPeriods) {
            mPeriods.add(period);
            Collections.sort(mPeriods);
        }
    }

    public int getDayOfWeek() {
        return mDayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.mDayOfWeek = dayOfWeek;
    }

    ///// GETTERS /////

    /**
     * Gets the periods of this day given the current group number. Fast; saves the periods every
     * Calendar the method is called.
     *
     * @param groupN the current group number
     */
    public List<Period> getPeriods(int groupN) {
        // Only refreshes truncated periods if the group number has changed
        if (groupN >= 0 && this.currentGroupN != groupN) {
            this.currentGroupN = groupN;
            mTruncatedPeriods = new ArrayList<>();
            for (Period p : mPeriods) {
                if (p.isInGroup(groupN)) mTruncatedPeriods.add(p);
            }
        }
        return mTruncatedPeriods;
    }

    /**
     * Gets the day of week of this Day in String form.
     *
     * @return day of week (Sunday - Saturday)
     */
    public String getDayOfWeekAsString() {
        switch (mDayOfWeek) {
            case Calendar.SUNDAY:
                return "Sunday";
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
        }
        return "Invalid day.";
    }

    /**
     * Returns the names of the groups, with the item in the 0th index empty.
     *
     * @return the group names for this day
     */
    public String[] getGroupNames() {
        return mGroupNames;
    }

    public static final String[] NO_GROUPS = new String[]{"No groups"};

    public boolean hasGroups() {
        return mGroupNames != null && mGroupNames.length > 1;
    }

    public List<Period> getAllPeriods() {
        return mPeriods;
    }

    /**
     * Adds all the given periods.
     *
     * @param periods list of {@link Period}s to be added
     */
    public void addPeriods(List<Period> periods) {
        for (Period p : periods) {
            addPeriod(p);
        }
    }

    public Period getPeriod(int pos) {
        return getPeriods(currentGroupN).get(pos);
    }

    @Override
    public String toString() {
        return SHelper.getStringDay(mDayOfWeek) + ", " + mPeriods.size() + " total periods.\n" +
                Arrays.toString(mGroupNames);
    }
    // STATIC METHODS

    /**
     * Gets a default day, based on the day of the week.
     *
     * @return the loaded Day object. null if the base day hasn't been loaded
     */
    public static Day getBaseDay(int dayOfWeek) {
        Day day = baseDays[dayOfWeek - Calendar.MONDAY];
        if (day == null) day = new Day(dayOfWeek, NO_GROUPS);
        return day;

        /*switch (dayOfWeek) {
            case Calendar.MONDAY:
            case Calendar.TUESDAY:
            case Calendar.THURSDAY:
            case Calendar.FRIDAY:
                day = new Day(dayOfWeek, DEFAULT_GROUPS);
                day.addPeriod(new Period("01", 7, 30, 8, 24, 0));
                day.addPeriod(new Period("02", 8, 30, 9, 24, 0));
                day.addPeriod(new Period("03", 9, 30, 10, 24, 0));
                day.addPeriod(new Period("LA", 10, 30, 11, 0, 1));
                day.addPeriod(new Period("04", 11, 6, 12, 0, 1));
                day.addPeriod(new Period("04", 10, 30, 11, 24, 2));
                day.addPeriod(new Period("LB", 11, 30, 12, 0, 2));
                day.addPeriod(new Period("05", 12, 6, 13, 0, 0));
                day.addPeriod(new Period("06", 13, 6, 14, 0, 0));
                break;
            case Calendar.WEDNESDAY:
                day = new Day(dayOfWeek);
                day.addPeriod(new Period("01", 7, 30, 8, 10, 0));
                day.addPeriod(new Period("02", 8, 16, 8, 56, 0));
                day.addPeriod(new Period("HR", 9, 2, 9, 12, 0));
                day.addPeriod(new Period("03", 9, 12, 9, 52, 0));
                day.addPeriod(new Period("04", 9, 58, 10, 38, 0));
                day.addPeriod(new Period("05", 10, 44, 11, 24, 0));
                day.addPeriod(new Period("06", 11, 30, 12, 10, 0));
                day.addPeriod(new Period("LN", 12, 10, 12, 30, 0));
                break;
        }
        return day;*/
    }

    /**
     * Gets a day for a holiday.
     *
     * @param name the name of the holiday
     */
    public static Day getHoliday(int dayOfWeek, String name) {
        Day day = new Day(dayOfWeek);

        day.addPeriod(Period.getHoliday(name));

        return day;
    }

    public static Day newFromParse(ParseObject obj, List<ParseObject> periods) {
        int dayOfWeek = obj.getInt(Day.DAY_OF_WEEK_KEY);
        JSONArray parseGroups = obj.getJSONArray(Day.GROUPS_KEY);

        Day day = new Day(dayOfWeek, SHelper.jsonArrayToStringArray(parseGroups));

        for (ParseObject p : periods) {
            day.addPeriod(Period.newFromParse(p));
        }

        return day;
    }

    public static final Day[] baseDays = new Day[Calendar.FRIDAY - Calendar.MONDAY + 1];

    public static void addBaseDay(Day day) {
        synchronized (baseDays) {
            baseDays[day.getDayOfWeek() - Calendar.MONDAY] = day;
        }
    }

    public static int nOfBaseDays() {
        int len = 0;
        for (Day day : baseDays) {
            if (day != null) len++;
        }
        return len;
    }

    public static void clearBaseDays() {
        for (int i = 0; i < baseDays.length; i++) baseDays[i] = null;
    }
}
