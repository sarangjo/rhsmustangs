package com.sarangjoshi.rhsmustangs.content;

import android.text.format.Time;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sarang on 4/6/2015.
 */
public class SSchedule {
    private List<SDay> mDays;

    public SSchedule() {
        mDays = new ArrayList<>();
    }

    public SSchedule loadDefaultSchedule() {
        for (int i = Time.MONDAY; i <= Time.FRIDAY; i++) {
            mDays.add(new SDay(i).loadDefaultPeriods());
        }
        return this;
    }

    public List<SDay> getDays() {
        return mDays;
    }

    /**
     * Gets the day from the schedule given a day of the week.
     *
     * @param dayOfWeek Monday = 1
     */
    public SDay getDay(int dayOfWeek) {
        return mDays.get(dayOfWeek - 1);
    }
}
