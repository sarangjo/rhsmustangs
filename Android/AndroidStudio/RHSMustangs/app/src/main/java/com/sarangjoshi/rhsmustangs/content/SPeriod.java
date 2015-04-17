package com.sarangjoshi.rhsmustangs.content;

import android.text.format.Time;

/**
 * Created by Sarang on 4/6/2015.
 */
public class SPeriod {
    public String mPeriodShort = "0";
    public String mClassName = "Period";
    public boolean isCustomizable = true;
    public STime mStartTime = new STime(6, 30);
    public STime mEndTime = new STime(7, 24);
    public int groupN = '0';

    public enum PeriodStyle {
        HOMEROOM, LUNCH, CLASS, OTHER
    }
    public enum TimeStyle {
        START, END
    }

    /**
     * Creates a new Period with the given parameters.
     * @param periodShort short name for the period
     * @param periodName period name
     * @param sh start hour
     * @param sm start minute
     * @param eh end hour
     * @param em end minute
     * @param gn group number
     */
    public SPeriod(String periodShort, String periodName, int sh, int sm,
                   int eh, int em, int gn) {
        mPeriodShort = periodShort;
        mClassName = periodName;
        mStartTime = new STime(sh, sm);
        mEndTime = new STime(eh, em);
        groupN = gn;
    }

    /**
     * Gets the start or end time as a string
     * @param style start or end
     * @param is24hr 24hr style or not
     */
    public String getTimeAsString(TimeStyle style, boolean is24hr) {
        return (style == TimeStyle.START ? mStartTime : mEndTime).toString(is24hr);
    }

    @Override
    public String toString() {
        String s = new String(mPeriodShort);
        s += " " + mClassName;
        s += ", " + getTimeAsString(TimeStyle.START, true);
        s += "-" + getTimeAsString(TimeStyle.END, true);
        return s;
    }

    /**
     * Gets this period's period style.
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

    public static SPeriod holiday(String holName) {
        return new SPeriod("HD", holName, 0, 0, 23, 59, 0);
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

        public STime(Time time) {
            this(time.hour, time.minute);
        }

        /*public boolean isBefore(STime t) {
            if (hour < t.hour)
                return true;
            else if (hour == t.hour && minute < t.minute)
                return true;
            else
                return false;
        }

        public boolean isAfter(STime t) {
            if (hour > t.hour)
                return true;
            else if (hour == t.hour && minute > t.minute)
                return true;
            else
                return false;
        }*/

        /**
         * -1 if this is less than other
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
         * @param is24hr
         * @return
         */
        public String toString(boolean is24hr) {
            String h = "", p = (hour >= 12) ? "pm" : "am";
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

            return h + ":" + m + p;
        }
    }
}

