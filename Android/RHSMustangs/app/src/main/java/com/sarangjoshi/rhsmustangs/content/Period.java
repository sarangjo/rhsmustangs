package com.sarangjoshi.rhsmustangs.content;

import android.support.annotation.NonNull;

import com.parse.ParseObject;

import java.util.Calendar;

/**
 * Represents a period in a schedule.
 *
 * @author Sarang
 */
public class Period implements Comparable<Period> {
    private static final String DEFAULT_NAME = "-";

    private String mPeriodShort;
    private String mClassName;
    private STime mStartTime;
    private STime mEndTime;
    private int mGroupN;

    private String mNote;

    /**
     * The default group number; for example, on init, for days with no groups, etc.
     */
    public static final int DEFAULT_GROUP_N = 1;
    /**
     * The index representing no groups. Used for periods that are independent of specific groups.
     */
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
    public Period(String periodShort, String name, int sh, int sm,
                  int eh, int em, int gn) {
        mPeriodShort = periodShort;
        mClassName = name;
        mStartTime = new STime(sh, sm);
        mEndTime = new STime(eh, em);
        mGroupN = gn;
        mNote = null;
    }

    public Period(String periodShort, int sh, int sm,
                  int eh, int em, int gn) {
        this(periodShort, DEFAULT_NAME, sh, sm, eh, em, gn);
    }

    /**
     * Guaranteed to be 2 characters long.
     */
    public String getShort() {
        return (mPeriodShort.length() < 2) ? " " + mPeriodShort : mPeriodShort.substring(0, 2);
    }

    public String getClassName() {
        if (mClassName.equals(DEFAULT_NAME))
            return getDefaultPeriodName();
        return getRawClassName();
    }

    public STime getStart() {
        return mStartTime;
    }

    public STime getEnd() {
        return mEndTime;
    }

    public int getGroupN() {
        return mGroupN;
    }

    public String getRawClassName() {
        return mClassName;
    }

    @Override
    public int compareTo(@NonNull Period other) {
        int startD = mStartTime.compareTo(other.mStartTime);
        if (startD == 0)
            return mEndTime.compareTo(other.mEndTime);
        return startD;
    }

    /**
     * @return null if empty; will never be the empty string
     */
    public String getNote() {
        if (mNote != null && mNote.isEmpty()) return null;
        return mNote;
    }

    public void setNote(String note) {
        if (note != null && !note.isEmpty())
            mNote = note;
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
     */
    public PeriodStyle getPeriodStyle() {
        if (mPeriodShort.charAt(0) == 'L')
            return PeriodStyle.LUNCH;
        else if (mPeriodShort.equals("HR"))
            return PeriodStyle.HOMEROOM;
        else {
            try {
                Integer.parseInt(mPeriodShort);
                return PeriodStyle.CLASS;
            } catch (Exception e) {
                return PeriodStyle.OTHER;
            }
        }
    }

    /**
     * Gets the default period name to display regardless of user settings.
     *
     * @return the period name
     */
    public String getDefaultPeriodName() {
        switch (getPeriodStyle()) {
            case CLASS:
                return "Period " + Integer.parseInt(mPeriodShort);
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
     */
    public boolean isInGroup(int groupN) {
        return this.mGroupN == BASE_GROUPN || this.mGroupN == groupN;
    }

    /**
     * Gets a getHoliday Period.
     *
     * @param holName the name of the getHoliday
     */
    public static Period getHoliday(String holName) {
        return new Period("HD", holName, 0, 0, 23, 59, 0);
    }

    /**
     * @param obj a fetched ParseObject representing a period
     */
    public static Period newFromParse(ParseObject obj) {
        Period p = new Period(obj.getString(SHORT_KEY),
                obj.getString(NAME_KEY),
                obj.getInt(START_HR_KEY),
                obj.getInt(START_MIN_KEY),
                obj.getInt(END_HR_KEY),
                obj.getInt(END_MIN_KEY),
                obj.getInt(GROUP_KEY));

        String note = obj.getString(NOTE_KEY);
        if (note != null && !note.isEmpty())
            p.setNote(note);
        return p;
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
                hour += minute / 60;
                minute = nM % 60;
            } else if (minute < 0) {
                hour += minute / 60;
                minute = nM % 60;
            } else
                minute = nM;
        }

        /**
         * -1 if this is less than other
         *
         * @param t the other
         */
        public int compareTo(Calendar t) {
            int totalMinutes = hour * 60 + minute;
            int otherMinutes = t.get(Calendar.HOUR_OF_DAY) * 60 + t.get(Calendar.MINUTE);
            return totalMinutes - otherMinutes;
        }

        /**
         * Given whether this time is 24 hour or not, returns a String representation.
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

        public int compareTo(@NonNull STime other) {
            if (this.hour == other.hour) {
                return this.minute - other.minute;
            }
            return this.hour - other.hour;
        }
    }

    // TODO: include this in creation of periods
    //public static String[] RESTRICTED_SHORTS = new String[]{"HR", "LN", "LA", "LB"};

    // Parse keys
    public static String SHORT_KEY = "short";
    public static String NAME_KEY = "name";
    public static String GROUP_KEY = "groupN";
    public static String START_HR_KEY = "startHr";
    public static String START_MIN_KEY = "startMin";
    public static String END_HR_KEY = "endHr";
    public static String END_MIN_KEY = "endMin";
    public static String NOTE_KEY = "note";
}
