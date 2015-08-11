package com.sarangjoshi.rhsmustangs.content;

import com.parse.ParseObject;
import com.sarangjoshi.rhsmustangs.helper.SHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SimpleTimeZone;

/**
 * Created by Sarang on 4/6/2015.
 */
public class SUpdatedDay extends SDay implements Comparable<SUpdatedDay> {
    private Calendar mDate;
    private int mGroupN = 1;

    /**
     * @param date
     * @param groupNames do not leave the 0th element empty
     */
    public SUpdatedDay(Calendar date, String[] groupNames) {
        super(date.get(Calendar.DAY_OF_WEEK), groupNames);
        mDate = date;
    }

    // TODO: rewrite using SHelper.getAbsDay()
    public boolean isToday(Calendar today) {
        return mDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && mDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && mDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
    }

    public static final String DATE_KEY = "date";
    public static final String GRP_NAMES_KEY = "groupNames";
    public static final String PERIODS_KEY = "periods";

    public static final String UPDATED_DAY_CLASS = "UpdatedDay";

    public Calendar getDate() {
        return mDate;
    }

    @Override
    public String toString() {
        return SHelper.getDisplayString(mDate);
    }

    public int compareTo(Calendar otherDay) {
        return SHelper.getAbsDay(this.getDate()) - SHelper.getAbsDay(otherDay);
    }

    @Override
    public int compareTo(SUpdatedDay another) {
        return compareTo(another.getDate());
    }

    public int getGroupN() {
        return mGroupN;
    }

    public boolean setGroupN(int groupN) {
        if (this.mGroupN != groupN) {
            this.mGroupN = groupN;
            return true;
        }
        return false;
    }

    /**
     * Creates a new {@link SUpdatedDay} from Parse data.
     *
     * @param obj     a downloaded ParseObject of the class {@link }
     * @param periods downloaded ParseObjects of the class {@link }
     * @return
     */
    public static SUpdatedDay newFromParse(ParseObject obj, List<ParseObject> periods) {
        Date parseDate = obj.getDate(DATE_KEY);
        JSONArray parseGroupNames = obj.getJSONArray(GRP_NAMES_KEY);

        Calendar date = SHelper.dateToCalendar(parseDate);
        String[] groupNames = null;
        if (parseGroupNames != null && parseGroupNames.length() > 0) {
            groupNames = new String[parseGroupNames.length()];
            try {
                for (int i = 0; i < parseGroupNames.length(); i++) {
                    groupNames[i] = parseGroupNames.getString(i);
                }
            } catch (JSONException ignored) {

            }
        }
        SUpdatedDay uDay = new SUpdatedDay(date, groupNames);

        for (ParseObject p : periods) {
            uDay.addPeriod(SPeriod.newFromParse(p));
        }

        return uDay;
    }

    public static SUpdatedDay test(Calendar date, String[] groupNames,
                                   SPeriod... periods) {
        SUpdatedDay uDay = new SUpdatedDay(date, groupNames);
        for (SPeriod p : periods) {
            uDay.addPeriod(p);
        }
        return uDay;
    }

    public static SUpdatedDay test1() {
        return test(new GregorianCalendar(2015, Calendar.JULY, 31),
                new String[]{"Grp1", "Grp2", "Grp3"},
                new SPeriod("01", 7, 30, 8, 24, 0),
                new SPeriod("02", 8, 30, 9, 24, 1),
                new SPeriod("02", 8, 30, 10, 24, 2),
                new SPeriod("02", 8, 30, 11, 24, 3));
    }

    public static SUpdatedDay test2() {
        return test(new GregorianCalendar(2015, Calendar.JULY, 30),
                new String[]{"Grp1", "Grp2"},
                new SPeriod("01", 7, 30, 8, 24, 0));
    }

    public static SUpdatedDay test3() {
        return test(new GregorianCalendar(2015, Calendar.JULY, 23),
                new String[]{"Grp1", "Grp2"},
                new SPeriod("01", "OMG", 7, 30, 14, 30, 1),
                new SPeriod("01", "OMG", 14, 30, 20, 30, 2));
    }
}
