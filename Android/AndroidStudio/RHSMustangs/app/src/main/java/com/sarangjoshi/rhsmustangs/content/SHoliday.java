package com.sarangjoshi.rhsmustangs.content;

import android.support.annotation.NonNull;

import com.parse.ParseObject;
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
public class SHoliday implements Comparable<SHoliday> {
    public static final String HOLIDAY_CLASS = "Holiday";
    private static final String HOLIDAY_NAME_KEY = "name";
    private static final String HOLIDAY_START_KEY = "start";
    private static final String HOLIDAY_END_KEY = "end";

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

    /**
     * Checks if the given day is contained in the holiday.
     */
    public boolean contains(Calendar day) {
        int start = SHelper.compareAbsDays(mStart, day);
        int end = SHelper.compareAbsDays(mEnd, day);

        return start <= 0 && end >= 0;

        //return mStart.compareTo(day) <= 0 && mEnd.compareTo(day) >= 0;
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

    public String getName() {
        return mName;
    }

    @Override
    public int compareTo(@NonNull SHoliday other) {
        int startD = mStart.compareTo(other.mStart);
        if(startD == 0)
            return mEnd.compareTo(other.mEnd);
        return startD;
    }

    public static SHoliday newFromParse(ParseObject parseObject) {
        String name = parseObject.getString(HOLIDAY_NAME_KEY);
        Calendar start = SHelper.stringToCalendar(parseObject.getString(HOLIDAY_START_KEY));
        Calendar end = SHelper.stringToCalendar(parseObject.getString(HOLIDAY_END_KEY));

        return new SHoliday(name, start, end);
    }
}
