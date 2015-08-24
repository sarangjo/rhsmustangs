package com.sarangjoshi.rhsmustangs.content;

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
public class SHoliday {
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

    public static SHoliday newFromParse(ParseObject parseObject) {
        String name = parseObject.getString(HOLIDAY_NAME_KEY);
        Calendar start = SHelper.dateToCalendar(parseObject.getDate(HOLIDAY_START_KEY));
        Calendar end = SHelper.dateToCalendar(parseObject.getDate(HOLIDAY_END_KEY));

        return new SHoliday(name, start, end);
    }
}
