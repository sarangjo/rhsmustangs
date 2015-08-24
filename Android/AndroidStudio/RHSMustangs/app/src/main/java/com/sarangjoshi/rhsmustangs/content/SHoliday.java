package com.sarangjoshi.rhsmustangs.content;

import com.sarangjoshi.rhsmustangs.helper.SHelper;

import java.util.Calendar;

/**
 * An {@link SDay} that is always displayed when viewing this getHoliday
 * A date range (start to end, both inclusive)
 * <p/>
 * Schedule checks holidays first, then the week
 *
 * @author Sarang Joshi
 */
public class SHoliday {
    public String mName;
    private SDay mDay;
    private Calendar mStart, mEnd;

    public SHoliday(String name, Calendar start, Calendar end) {
        mName = name;
        mDay = SDay.getHoliday(start.get(Calendar.DAY_OF_WEEK), name);
        mStart = start;
        mEnd = end;
    }

    public SDay getDay(int dayOfWeek) {
        // TODO: ???
        mDay.setDayOfWeek(dayOfWeek);
        return mDay;
    }

    public boolean contains(Calendar day) {
        return mStart.compareTo(day) <= 0 && mEnd.compareTo(day) >= 0;
    }

    public String toString() {
        String s = "";
        s += mName + ", " + SHelper.getShortString(mStart);
        s += " to " + SHelper.getShortString(mEnd);
        return s;
    }

    public Calendar getStart() {
        return mStart;
    }

    public Calendar getEnd() {
        return mEnd;
    }
}
