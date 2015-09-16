package com.sarangjoshi.rhsmustangs.content;

import com.sarangjoshi.rhsmustangs.helper.SHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This object keeps track of a collection of days, representing a full schedule.
 * Created by Sarang on 4/6/2015.
 */
public class Week {
    private List<Day> mDays;
    //private int currentDay;

    public Week() {
        mDays = new ArrayList<>();
    }

    public void addDay(Day day) {
        mDays.add(day);
    }

    /**
     * Gets the day from the schedule given a day of the week.
     *
     * @param dayOfWeek based on {@link Calendar}
     */
    public Day getDay(int dayOfWeek) {
        return mDays.get(getIndex(dayOfWeek));
    }

    /**
     * Sets the day for a given day of the week.
     */
    public void setDay(int dayOfWeek, Day day) {
        mDays.set(getIndex(dayOfWeek), day);
    }

    /**
     * Given a day-of-week (MONDAY to FRIDAY) gives the corresponding index in the day list. Automatically
     * rounds up for SATURDAY and SUNDAY.
     *
     * @param dayOfWeek the day of the week, according to {@link Calendar}.
     */
    private int getIndex(int dayOfWeek) {
        if (dayOfWeek < Calendar.MONDAY || dayOfWeek > Calendar.FRIDAY) {
            return 0;
        }
        return dayOfWeek - Calendar.MONDAY;
    }

    /**
     * Updates the week's days with the appropriate updated days.
     *
     * @param today       the current day
     * @param updatedDays the list of updated days to filter and add into the current week as needed
     */
    public void update(Calendar today, List<UpdatedDay> updatedDays) {
        Calendar monday = SHelper.getRelativeDay(today, Calendar.MONDAY);
        Calendar friday = SHelper.getRelativeDay(today, Calendar.FRIDAY);
        for (UpdatedDay day : updatedDays) {
            if (day.compareTo(monday) >= 0 && day.compareTo(friday) <= 0) {
                setDay(day.getDayOfWeek(), day);
            }
        }
    }

    /**
     * Loads the default schedule based on the default periods as per the {@link Day} default
     * periods.
     */
    public static Week getDefaultWeek() {
        Week week = new Week();
        for (int i = Calendar.MONDAY; i <= Calendar.FRIDAY; i++) {
            week.addDay(Day.getBaseDay(i));
        }
        return week;
    }

}
