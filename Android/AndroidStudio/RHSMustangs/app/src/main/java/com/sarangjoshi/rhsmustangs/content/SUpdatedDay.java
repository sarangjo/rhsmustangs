package com.sarangjoshi.rhsmustangs.content;

import android.text.format.Time;

/**
 * Created by Sarang on 4/6/2015.
 */
public class SUpdatedDay extends SDay {
    Time mDate;

    public SUpdatedDay(Time date, String[] groupNames) {
        super(date.weekDay, groupNames);
        mDate = date;
    }
}
