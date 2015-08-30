package com.sarangjoshi.rhsmustangs.content;

import android.support.annotation.NonNull;

import com.parse.ParseObject;
import com.sarangjoshi.rhsmustangs.helper.SHelper;

import org.json.JSONArray;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Represents an updated day in the schedule.
 *
 * @author Sarang
 */
public class SUpdatedDay extends SDay implements Comparable<SUpdatedDay> {
    private Calendar mDate;
    private int mGroupN = 1;

    /**
     * Initializes a new updated day.
     *
     * @param groupNames do not leave the 0th element empty
     */
    public SUpdatedDay(Calendar date, String[] groupNames) {
        super(date.get(Calendar.DAY_OF_WEEK), groupNames);
        mDate = date;
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
        return SHelper.compareAbsDays(this.getDate(), otherDay);
    }

    @Override
    public int compareTo(@NonNull SUpdatedDay other) {
        return compareTo(other.getDate());
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
     */
    public static SUpdatedDay newFromParse(ParseObject obj, List<ParseObject> periods) {
        //Date parseDate = obj.getDate(DATE_KEY);
        String parseDate = obj.getString(DATE_KEY);
        JSONArray parseGroupNames = obj.getJSONArray(GRP_NAMES_KEY);

        Calendar date = SHelper.stringToCalendar(parseDate);
        String[] groupNames = SHelper.jsonArrayToStringArray(parseGroupNames);
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

    public static SUpdatedDay testPeriodSorting() {
        return test(new GregorianCalendar(2015, Calendar.JULY, 23),
                new String[]{"Grp1", "Grp2"},
                new SPeriod("01", "OMG", 7, 30, 14, 30, 1),
                new SPeriod("01", "OMG", 14, 30, 20, 30, 2));
    }
}
