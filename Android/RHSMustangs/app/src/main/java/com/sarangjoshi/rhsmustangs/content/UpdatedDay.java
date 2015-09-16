package com.sarangjoshi.rhsmustangs.content;

import android.support.annotation.NonNull;

import com.parse.ParseObject;
import com.sarangjoshi.rhsmustangs.helper.SHelper;

import org.json.JSONArray;

import java.util.Calendar;
import java.util.List;

/**
 * Represents an updated day in the schedule.
 *
 * @author Sarang
 */
public class UpdatedDay extends Day implements Comparable<UpdatedDay> {
    private Calendar mDate;
    private int mGroupN = 1;

    /**
     * Initializes a new updated day.
     *
     * @param groupNames do not leave the 0th element empty
     */
    public UpdatedDay(Calendar date, String[] groupNames) {
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
    public int compareTo(@NonNull UpdatedDay other) {
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
     * Creates a new {@link UpdatedDay} from Parse data.
     *
     * @param obj     a downloaded ParseObject of the class {@link }
     * @param periods downloaded ParseObjects of the class {@link }
     */
    public static UpdatedDay newFromParse(ParseObject obj, List<ParseObject> periods) {
        //Date parseDate = obj.getDate(DATE_KEY);
        String parseDate = obj.getString(DATE_KEY);
        JSONArray parseGroupNames = obj.getJSONArray(GRP_NAMES_KEY);

        Calendar date = SHelper.stringToCalendar(parseDate);
        String[] groupNames = SHelper.jsonArrayToStringArray(parseGroupNames);
        UpdatedDay uDay = new UpdatedDay(date, groupNames);

        for (ParseObject p : periods) {
            uDay.addPeriod(Period.newFromParse(p));
        }

        return uDay;
    }

    public static UpdatedDay test(Calendar date, String[] groupNames,
                                   Period... periods) {
        UpdatedDay uDay = new UpdatedDay(date, groupNames);
        for (Period p : periods) {
            uDay.addPeriod(p);
        }
        return uDay;
    }
}
