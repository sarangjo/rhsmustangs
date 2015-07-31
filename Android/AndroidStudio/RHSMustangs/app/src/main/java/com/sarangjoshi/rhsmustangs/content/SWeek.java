package com.sarangjoshi.rhsmustangs.content;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This object keeps track of a collection of days, representing a full schedule.
 * Created by Sarang on 4/6/2015.
 */
public class SWeek {
    private List<SDay> mDays;
    //private int currentDay;

    public SWeek() {
        mDays = new ArrayList<>();
    }

    /**
     * Returns a deep copy of this schedule's days.
     *
     * @return a deep copy of this schedule's days.
     */
    public List<SDay> getDays() {
        List<SDay> days = new ArrayList<SDay>();
        for (SDay d : mDays) {
            days.add(d);
        }
        return days;
    }

    public void addDay(SDay day) {
        mDays.add(day);
    }

    /**
     * Gets the day from the schedule given a day of the week.
     *
     * @param dayOfWeek based on {@link Calendar}
     */
    public SDay getDay(int dayOfWeek) {
        return mDays.get(getIndex(dayOfWeek));
    }

    /**
     * Given a day-of-week (MONDAY to FRIDAY) gives the corresponding index in the day list. Automatically
     * rounds up for SATURDAY and SUNDAY.
     *
     * @param dayOfWeek the day of the week, according to {@link Calendar}.
     * @return
     */
    private int getIndex(int dayOfWeek) {
        if (dayOfWeek < Calendar.MONDAY || dayOfWeek > Calendar.FRIDAY) {
            return 0;
        }
        return dayOfWeek - Calendar.MONDAY;
    }

    /**
     * Loads the default schedule based on the default periods as per the {@link SDay} default
     * periods.
     */
    public static SWeek getDefaultWeek() {
        SWeek week = new SWeek();
        for (int i = Calendar.MONDAY; i <= Calendar.FRIDAY; i++) {
            week.addDay(SDay.getDefaultDay(i));
        }
        return week;
    }
}
