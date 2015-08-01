package com.sarangjoshi.rhsmustangs.content;

import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Sarang on 4/6/2015.
 */
public class SUpdatedDay extends SDay {
    private Calendar mDate;

    public SUpdatedDay(Calendar date, String[] groupNames) {
        super(date.get(Calendar.DAY_OF_WEEK), groupNames);
        mDate = date;
    }

    public boolean isToday(Calendar today) {
        return mDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && mDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && mDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
    }

    public static SUpdatedDay newFromParse(ParseObject obj, List<ParseObject> periods) {
        Date parseDate = obj.getDate(DATE_KEY);
        JSONArray parseGroupNames = obj.getJSONArray(GRP_NAMES_KEY);

        Calendar date = new GregorianCalendar(parseDate.getYear(), parseDate.getMonth(),
                parseDate.getDay());
        String[] groupNames = new String[parseGroupNames.length() + 1];
        try {
            for (int i = 0; i < parseGroupNames.length(); i++) {
                groupNames[i] = parseGroupNames.getString(i);
            }
        } catch (JSONException e) {

        }
        SUpdatedDay uDay = new SUpdatedDay(date, groupNames);

        for (ParseObject p : periods) {
            uDay.addPeriod(SPeriod.newFromParse(p));
        }

        return uDay;
    }

    public static SUpdatedDay test() {
        Calendar date = new GregorianCalendar(2015, Calendar.JULY, 31);
        String[] groupNames = new String[]{"Grp1", "Grp2"};

        SUpdatedDay uDay = new SUpdatedDay(date, groupNames);

        uDay.addPeriod(new SPeriod("1", "-", 7, 30, 8, 24, 0));
        uDay.addPeriod(new SPeriod("2", "-", 8, 30, 9, 24, 1));
        uDay.addPeriod(new SPeriod("2", "-", 8, 30, 10, 24, 2));

        return uDay;
    }

    public static SUpdatedDay test2() {
        Calendar date = new GregorianCalendar(2015, Calendar.JULY, 30);
        String[] groupNames = new String[]{"Grp1", "Grp2"};

        SUpdatedDay uDay = new SUpdatedDay(date, groupNames);

        uDay.addPeriod(new SPeriod("1", "-", 7, 30, 8, 24, 0));

        return uDay;
    }

    public static final String DATE_KEY = "date";
    public static final String GRP_NAMES_KEY = "groupNames";
    public static final String PERIODS_KEY = "periods";

    public Calendar getDate() {
        return mDate;
    }
}
