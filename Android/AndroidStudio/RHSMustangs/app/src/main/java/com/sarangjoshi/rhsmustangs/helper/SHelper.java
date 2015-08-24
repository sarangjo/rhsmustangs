package com.sarangjoshi.rhsmustangs.helper;

/**
 * Created by Sarang on 4/8/2015.
 */

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.Time;

import com.sarangjoshi.rhsmustangs.content.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class SHelper {
    private static final String NO_GROUPS = "[]";
    private static final int END_OF_DAY = 16;
    public static int RFC2445_DATE_LENGTH = 15;

    public static String COLOR_UPDATE = "#006600";
    public static String COLOR_HOLIDAY = "#D4AF37";// "#FFD700";

    public static String DEFAULT_OVERRIDE_NAME = "-";

    /*
     * Updates the current time in local variables now.
     *
     * @return the Time object that is the current time.

    public static Calendar updateCurrentTime() {
        now = new Time();
        now.setToNow();
        now.normalize(false);
        return now;
    }*/

    /*
     * Shifts the day of the given time by the number of days to shift
     *
     * @param time
     *            given time
     * @param dayChange
     *            number of days to shift time
     * @return shifted time
     */
    /*public static Time shiftDay(Time time, int dayChange, SData data) {
        Time t = new Time();
        t.set(0, data.getMiscDetail("shifted_min"),
                data.getMiscDetail("shifted_hour"), time.monthDay + dayChange,
                time.month, time.year);
        t.normalize(false);
        return t;
    }*/

    /**
     * Gets the absolute day.
     *
     * @param time
     * @return
     */
    public static int getAbsDay(Calendar time) {
        return Time.getJulianDay(time.getTimeInMillis(), time.getTimeZone().getRawOffset() / 1000);
    }

    public static ArrayList<String> getArrayListFromArray(String[] s) {
        ArrayList<String> a = new ArrayList<String>();
        for (int i = 0; i < s.length; i++) {
            a.add(s[i]);
        }
        return a;
    }

    public static String shortenCustomGrp(String string) {
        string = string.substring(string.indexOf(" ") + 1);
        return (string = string.substring(string.indexOf(" ") + 1));
    }


    public static Bitmap decodeBitmapFromRes(Resources res, int resId, int w,
                                             int h) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, w, h);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Trims off the end open and close carrots, if any.
     */
    public static String trimCarrots(String s) {
        int iOfOpen = s.indexOf('<');
        int iOfClose = s.indexOf('>');
        if (iOfOpen >= 0) {
            s = s.substring(iOfOpen + 2);
        }
        if (iOfClose >= 0) {
            s = s.substring(0, iOfClose - 2);
        }
        return s;
    }

    /**
     * Gets the display String.
     *
     * @param date
     * @return
     */
    public static String getDisplayString(Calendar date) {
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
        format.setTimeZone(date.getTimeZone());
        return format.format(date.getTime());
    }

    public static String getShortString(Calendar date) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
        format.setTimeZone(date.getTimeZone());
        return format.format(date.getTime());
    }

    public static Calendar getRelativeDay(Calendar today, int day) {
        Calendar relative = (Calendar) today.clone();
        relative.add(Calendar.DAY_OF_MONTH, day - today.get(Calendar.DAY_OF_WEEK));
        return relative;
    }

    public static int getAbsDifference(Calendar first, Calendar second) {
        return getAbsDay(first) - getAbsDay(second);
    }

    /**
     * Checks whether the given {@link Calendar}s are in the same week.
     *
     * @param first
     * @param second
     * @return
     */
    public static boolean sameWeek(Calendar first, Calendar second) {
        // TODO: fix
        return first.get(Calendar.WEEK_OF_YEAR) == second.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * Converts a Date object to a Calendar object.
     */
    public static Calendar dateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String dateToString(Calendar date) {
        DATE_FORMAT.setTimeZone(date.getTimeZone());
        return DATE_FORMAT.format(date.getTime());
    }

    /**
     * @param string
     * @return null if incorrectly formatted string
     */
    public static Calendar stringToCalendar(String string) {
        try {
            return dateToCalendar(DATE_FORMAT.parse(string));
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Each group name surrounded by quotes, separated by commas, no spaces in between groups.
     *
     * @return
     */
    public static String groupsToString(String[] groupNames) {
        if (groupNames == null || groupNames.length == 0) {
            return NO_GROUPS;
        }
        String s = "[\"" + groupNames[0] + "\"";
        for (int i = 1; i < groupNames.length; i++) {
            s += ",\"" + groupNames[i] + "\"";
        }
        return s + "]";
    }

    /**
     *
     * @param groupsString not null
     * @return null if there are no groups
     */
    public static String[] stringToGroups(String groupsString) {
        if (groupsString.equals(NO_GROUPS)) {
            return null;
        }
        String[] groups = groupsString.split("\",\"");
        // Cut leading [" and trailing "]
        groups[0] = groups[0].substring(2);
        String end = groups[groups.length - 1];
        groups[groups.length - 1] = end.substring(0, end.length() - 2);

        return groups;
    }

    /**
     * Gets the actual today Calendar
     * @return
     */
    public static Calendar getActualToday() {
        // DEBUG: Fix to actual
        Calendar cal = new GregorianCalendar();
        //new GregorianCalendar(2015, Calendar.AUGUST, 28, 18, 0, 0);
        if(cal.get(Calendar.HOUR_OF_DAY) > END_OF_DAY) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return cal;
    }
}
