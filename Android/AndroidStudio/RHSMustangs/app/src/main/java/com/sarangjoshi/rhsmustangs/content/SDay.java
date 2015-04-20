package com.sarangjoshi.rhsmustangs.content;

import android.text.format.Time;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sarang on 4/6/2015.
 */
public class SDay {
    List<SPeriod> mPeriods;
    int mDayOfWeek;

    public SDay(int dayOfWeek) {
        mDayOfWeek = dayOfWeek;
        mPeriods = new ArrayList<>();
    }

    public void addPeriod(int i, SPeriod period) {
        mPeriods.add(i, period);
    }

    public void addPeriod(SPeriod period) {
        addPeriod(mPeriods.size(), period);
    }

    public SDay loadDefaultPeriods() {
        switch(mDayOfWeek) {
            case Time.MONDAY:
            case Time.TUESDAY:
            case Time.THURSDAY:
            case Time.FRIDAY:
                addPeriod(new SPeriod("1", "-", 7, 30, 8, 24, 0));
                addPeriod(new SPeriod("2", "-", 8, 30, 9, 24, 0));
                addPeriod(new SPeriod("3", "-", 9, 30, 10, 24, 0));
                addPeriod(new SPeriod("LA", "-", 10, 30, 11, 0, 1));
                addPeriod(new SPeriod("4", "-", 11, 6, 12, 0, 1));
                addPeriod(new SPeriod("4", "-", 10, 30, 11, 24, 2));
                addPeriod(new SPeriod("LB", "-", 11, 30, 12, 0, 2));
                addPeriod(new SPeriod("5", "-", 12, 6, 13, 0, 0));
                addPeriod(new SPeriod("6", "-", 13, 6, 14, 0, 0));
                break;
            case Time.WEDNESDAY:
                addPeriod(new SPeriod("1", "-", 7, 30, 8, 10, 0));
                addPeriod(new SPeriod("2", "-", 8, 16, 8, 56, 0));
                addPeriod(new SPeriod("HR", "Homeroom", 9, 2, 9, 12, 0));
                addPeriod(new SPeriod("3", "-", 9, 12, 9, 52, 0));
                addPeriod(new SPeriod("4", "-", 9, 58, 10, 38, 0));
                addPeriod(new SPeriod("5", "-", 10, 44, 11, 24, 0));
                addPeriod(new SPeriod("6", "-", 11, 30, 12, 10, 0));
                addPeriod(new SPeriod("LN", "-", 12, 10, 12, 30, 0));
                break;
        }
        return this;
    }

    public List<SPeriod> getPeriods() {
        return mPeriods;
    }

    public SPeriod getPeriod(int pos) {
        return mPeriods.get(pos);
    }

    public int getDayOfWeek() {
        return mDayOfWeek;
    }

    /**
     * Gets the day of week of this SDay in String form.
     * @return day of week (Sunday - Saturday)
     */
    public String getDayOfWeekAsString() {
        switch (mDayOfWeek) {
            case 0:
                return "Sunday";
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
        }
        return "Invalid day.";
    }
}
