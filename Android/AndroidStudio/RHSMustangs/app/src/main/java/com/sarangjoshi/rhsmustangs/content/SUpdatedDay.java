package com.sarangjoshi.rhsmustangs.content;

import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Sarang on 4/6/2015.
 */
public class SUpdatedDay extends SDay {
    private Calendar mDate;

    public SUpdatedDay(Calendar date, String[] groupNames) {
        super(date.get(Calendar.DAY_OF_WEEK), groupNames);
        mDate = date;
    }

    public static SUpdatedDay newFromParse(ParseObject obj, List<ParseObject> periods) {
        Date parseDate = (Date) obj.get(DATE_KEY);
        JSONArray parseGroupNames = (JSONArray) obj.get(GRP_NAMES_KEY);

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

        for(ParseObject p : periods) {
            uDay.addPeriod(SPeriod.newFromParse(p));
        }

        return uDay;
    }

    public static final String DATE_KEY = "date";
    public static final String GRP_NAMES_KEY = "groupNames";
    public static final String PERIODS_KEY = "periods";
}
