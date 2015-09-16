package com.sarangjoshi.rhsmustangs.helper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.Time;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Sarang
 */
public class SHelper {
    private static final String NO_GROUPS = "[]";
    private static final int END_OF_DAY = 16;
    public static int RFC2445_DATE_LENGTH = 15;

    public static String DEFAULT_OVERRIDE_NAME = "-";

    public static int getAbsDay(Calendar time) {
        time.set(Calendar.HOUR_OF_DAY, 0);
        return Time.getJulianDay(time.getTimeInMillis(), time.getTimeZone().getRawOffset() / 1000);
    }

    public static int compareAbsDays(Calendar time1, Calendar time2) {
        int yearDiff = time1.get(Calendar.YEAR) - time2.get(Calendar.YEAR);
        if (yearDiff != 0) return yearDiff;
        int monthDiff = time1.get(Calendar.MONTH) - time2.get(Calendar.MONTH);
        if (monthDiff != 0) return monthDiff;
        return time1.get(Calendar.DAY_OF_MONTH) - time2.get(Calendar.DAY_OF_MONTH);
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
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yy-MM-dd, HH:mm:ss");

    public static String calendarToString(Calendar date) {
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
     * Sets the text color of the given views.
     *
     * @param c the color
     */
    public static void setTextColor(int c, TextView... views) {
        for (TextView v : views) {
            v.setTextColor(c);
        }
    }

    /**
     * Gets the actual schedule-wise Calendar for today. If the hour of day is
     * after the predefined end of the schedule day, rolls over to the next day.
     */
    public static Calendar getScheduleWiseToday() {
        // DEBUG: Fix to actual
        Calendar cal = new GregorianCalendar();
        //new GregorianCalendar(2015, Calendar.AUGUST, 28, 18, 0, 0);
        if (cal.get(Calendar.HOUR_OF_DAY) > END_OF_DAY) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return cal;
    }

    public static String[] jsonArrayToStringArray(JSONArray array) {
        String[] a = null;
        if (array != null && array.length() > 0) {
            a = new String[array.length()];
            try {
                for (int i = 0; i < array.length(); i++) {
                    a[i] = array.getString(i);
                }
            } catch (JSONException ignored) {
            }
        }
        return a;
    }

    public static String dateToString(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String dateTimeToString(Date date) {
        return DATE_TIME_FORMAT.format(date);
    }

    public static Calendar stringToDateTimeCalendar(String string) {
        try {
            return dateToCalendar(DATE_TIME_FORMAT.parse(string));
        } catch (ParseException e) {
            return null;
        }
    }
}
