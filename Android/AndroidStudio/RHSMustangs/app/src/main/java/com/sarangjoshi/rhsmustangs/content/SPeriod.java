package com.sarangjoshi.rhsmustangs.content;

import com.parse.ParseObject;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Sarang on 4/6/2015.
 */
public class SPeriod {
    private String mPeriodShort;
    private String mClassName;
    private STime mStartTime;
    private STime mEndTime;
    private int mGroupN;

    public static final int BASE_GROUPN = 0;

    /**
     * Creates a new Period with the given parameters.
     *
     * @param periodShort short name for the period
     * @param name        period name
     * @param sh          start hour
     * @param sm          start minute
     * @param eh          end hour
     * @param em          end minute
     * @param gn          group number
     */
    public SPeriod(String periodShort, String name, int sh, int sm,
                   int eh, int em, int gn) {
        mPeriodShort = periodShort;
        mClassName = name;
        mStartTime = new STime(sh, sm);
        mEndTime = new STime(eh, em);
        mGroupN = gn;
    }

    public SPeriod(String periodShort, int sh, int sm,
                   int eh, int em, int gn) {
        mPeriodShort = periodShort;
        mClassName = getDefaultPeriodName();
        mStartTime = new STime(sh, sm);
        mEndTime = new STime(eh, em);
        mGroupN = gn;
    }

    public String getShort() {
        return mPeriodShort;
    }

    public String getClassName() {
        return mClassName;
    }

    public STime getStart() {
        return mStartTime;
    }

    public STime getEnd() {
        return mEndTime;
    }

    public enum PeriodStyle {
        HOMEROOM, LUNCH, CLASS, OTHER
    }

    public enum TimeStyle {
        START, END
    }

    /**
     * Gets the start or end time as a string
     *
     * @param style  start or end
     * @param is24hr 24hr style or not
     */
    public String getTimeAsString(TimeStyle style, boolean is24hr) {
        return (style == TimeStyle.START ? mStartTime : mEndTime).toString(is24hr);
    }

    @Override
    public String toString() {
        String s = mPeriodShort;
        s += " " + mClassName;
        s += ", " + getTimeAsString(TimeStyle.START, false);
        s += "-" + getTimeAsString(TimeStyle.END, false);
        return s;
    }

    /**
     * Gets this period's period style.
     *
     * @return
     */
    public PeriodStyle getPeriodStyle() {
        if (mPeriodShort.charAt(0) == 'L')
            return PeriodStyle.LUNCH;
        else if (mPeriodShort.equals("HR"))
            return PeriodStyle.HOMEROOM;
        else
            return PeriodStyle.CLASS;
    }

    /**
     * Gets the default period name to display regardless of user settings.
     *
     * @return the period name
     */
    public String getDefaultPeriodName() {
        switch (getPeriodStyle()) {
            case CLASS:
                return "Period " + mPeriodShort;
            case HOMEROOM:
                return "Homeroom";
            case LUNCH:
                return "Lunch";
            default:
                return mClassName;
        }
    }

    /**
     * Checks to see if the period is in the given group number.
     *
     * @param groupN
     * @return
     */
    public boolean isInGroup(int groupN) {
        return this.mGroupN == BASE_GROUPN || this.mGroupN == groupN;
    }

    /**
     * Gets a holiday SPeriod.
     *
     * @param holName the name of the holiday
     */
    public static SPeriod holiday(String holName) {
        return new SPeriod("HD", holName, 0, 0, 23, 59, 0);
    }

    public static SPeriod newFromParse(ParseObject obj) {
        try {
            return new SPeriod(obj.getString(SHORT_KEY),
            obj.getString(NAME_KEY),
            obj.getInt(START_HR_KEY),
            obj.getInt(START_MIN_KEY),
            obj.getInt(END_HR_KEY),
            obj.getInt(END_MIN_KEY),
            obj.getInt(GROUP_KEY));
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * A class to be used for the timings of periods; independent of date.
     *
     * @author Sarang Joshi
     */
    public static class STime implements Comparable<STime> {
        public int hour;
        public int minute;

        public STime(int nH, int nM) {
            hour = nH % 24;
            if (minute >= 60) {
                hour += (int) (minute / 60);
                minute = nM % 60;
            } else if (minute < 0) {
                hour += (int) (minute / 60);
                minute = nM % 60;
            } else
                minute = nM;
        }

        public STime(Calendar time) {
            this(time.get(GregorianCalendar.HOUR_OF_DAY), time.get(GregorianCalendar.MINUTE));
        }

        /**
         * -1 if this is less than other
         *
         * @param t the other
         * @return
         */
        public int compareTo(STime t) {
            int totalMinutes = hour * 60 + minute;
            int otherMinutes = t.hour * 60 + t.minute;
            return totalMinutes - otherMinutes;
        }

        /**
         * Given whether this time is 24 hour or not, returns a String representation.
         *
         * @param is24hr
         * @return
         */
        public String toString(boolean is24hr) {
            String h = "", amPm = (hour >= 12) ? "pm" : "am";
            if (is24hr) {
                h += hour;
            } else {
                if (hour == 0)
                    h += 12;
                else if (hour > 12) {
                    h += (hour % 12);
                } else
                    h += hour;
            }
            String m = "";
            m += (minute < 10) ? ("0" + minute) : minute;

            return h + ":" + m + ((!is24hr) ? amPm : "");
        }
    }

    public static String[] RESTRICTED_SHORTS = new String[]{"HR", "LN", "LA", "LB"};
    public static String SHORT_KEY = "short";
    public static String NAME_KEY = "name";
    public static String GROUP_KEY = "groupN";
    public static String START_HR_KEY = "startHr";
    public static String START_MIN_KEY = "startMin";
    public static String END_HR_KEY = "endHr";
    public static String END_MIN_KEY = "endMin";
}
