package com.sarangjoshi.rhsmustangs.content;

import android.text.format.Time;

import java.util.ArrayList;
import java.util.List;

public class SDay {
    private List<SPeriod> mPeriods;
    private int mDayOfWeek;

    private List<SPeriod> mTruncatedPeriods;
    private int groupN = -1;

    /**
     * Creates a new SDay with the given day of the week.
     *
     * @param dayOfWeek
     */
    public SDay(int dayOfWeek) {
        this.mDayOfWeek = dayOfWeek;
        this.mPeriods = new ArrayList<>();
    }

    public void addPeriod(int i, SPeriod period) {
        mPeriods.add(i, period);
    }

    public void addPeriod(SPeriod period) {
        addPeriod(mPeriods.size(), period);
    }

    /**
     * Gets the periods of this day given the current group number. Fast; saves the periods every
     * time the method is called.
     *
     * @param groupN the current group number
     * @return
     */
    public List<SPeriod> getPeriods(int groupN) {
        if(groupN != -1 || this.groupN != groupN) {
            this.groupN = groupN;
            mTruncatedPeriods = new ArrayList<SPeriod>();
            for (SPeriod p : mPeriods) {
                if (p.isInGroup(groupN)) mTruncatedPeriods.add(p);
            }
        }
        return mTruncatedPeriods;
    }

    /*public SPeriod getPeriod(int pos) {
        return mPeriods.get(pos);
    }*/

    public int getDayOfWeek() {
        return mDayOfWeek;
    }

    /**
     * Gets the day of week of this SDay in String form.
     *
     * @return day of week (Sunday - Saturday)
     */
    public String getDayOfWeekAsString() {
        switch (mDayOfWeek) {
            case Time.SUNDAY:
                return "Sunday";
            case Time.MONDAY:
                return "Monday";
            case Time.TUESDAY:
                return "Tuesday";
            case Time.WEDNESDAY:
                return "Wednesday";
            case Time.THURSDAY:
                return "Thursday";
            case Time.FRIDAY:
                return "Friday";
            case Time.SATURDAY:
                return "Saturday";
        }
        return "Invalid day.";
    }

    // TODO: fix

    /**
     * Loads the default periods, based on the day of the week.
     *
     * @return the loaded SDay object, for chaining
     */
    public static SDay getDefaultDay(int dayOfWeek) {
        SDay day = new SDay(dayOfWeek);

        switch (dayOfWeek) {
            case Time.MONDAY:
            case Time.TUESDAY:
            case Time.THURSDAY:
            case Time.FRIDAY:
                day.addPeriod(new SPeriod("1", "-", 7, 30, 8, 24, 0));
                day.addPeriod(new SPeriod("2", "-", 8, 30, 9, 24, 0));
                day.addPeriod(new SPeriod("3", "-", 9, 30, 10, 24, 0));
                day.addPeriod(new SPeriod("LA", "-", 10, 30, 11, 0, 1));
                day.addPeriod(new SPeriod("4", "-", 11, 6, 12, 0, 1));
                day.addPeriod(new SPeriod("4", "-", 10, 30, 11, 24, 2));
                day.addPeriod(new SPeriod("LB", "-", 11, 30, 12, 0, 2));
                day.addPeriod(new SPeriod("5", "-", 12, 6, 13, 0, 0));
                day.addPeriod(new SPeriod("6", "-", 13, 6, 14, 0, 0));
                break;
            case Time.WEDNESDAY:
                day.addPeriod(new SPeriod("1", "-", 7, 30, 8, 10, 0));
                day.addPeriod(new SPeriod("2", "-", 8, 16, 8, 56, 0));
                day.addPeriod(new SPeriod("HR", "Homeroom", 9, 2, 9, 12, 0));
                day.addPeriod(new SPeriod("3", "-", 9, 12, 9, 52, 0));
                day.addPeriod(new SPeriod("4", "-", 9, 58, 10, 38, 0));
                day.addPeriod(new SPeriod("5", "-", 10, 44, 11, 24, 0));
                day.addPeriod(new SPeriod("6", "-", 11, 30, 12, 10, 0));
                day.addPeriod(new SPeriod("LN", "-", 12, 10, 12, 30, 0));
                break;
        }
        return day;
    }

}
