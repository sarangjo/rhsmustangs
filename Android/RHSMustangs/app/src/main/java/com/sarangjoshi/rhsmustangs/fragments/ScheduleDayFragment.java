package com.sarangjoshi.rhsmustangs.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sarangjoshi.rhsmustangs.R;
import com.sarangjoshi.rhsmustangs.content.Day;
import com.sarangjoshi.rhsmustangs.content.Period;
import com.sarangjoshi.rhsmustangs.content.UpdatedDay;
import com.sarangjoshi.rhsmustangs.helper.SHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * TODO: add class comment
 *
 * @author Sarang Joshi
 */
public class ScheduleDayFragment extends Fragment {
    public Day mDay;
    public Calendar mCalendar;
    public int mGroupN;

    private ListView mListView;
    private ScheduleAdapter mAdapter;
    private TextView mTestView;

    public static ScheduleDayFragment newInstance(Day day, Calendar cal, int groupN) {
        ScheduleDayFragment fragment = new ScheduleDayFragment();
        fragment.mDay = day;
        fragment.mCalendar = cal;
        fragment.mGroupN = groupN;
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mDay != null)
            mAdapter = new ScheduleAdapter(getActivity(), mDay.getPeriods(mGroupN));
        else
            mAdapter = new ScheduleAdapter(getActivity(), new ArrayList<Period>());
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.schedule_day, container, false);

        mListView = (ListView) v.findViewById(R.id.viewPagerPeriodsList);
        mListView.setAdapter(mAdapter);
        mTestView = (TextView) v.findViewById(R.id.test);
        mTestView.setText(mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US) + " " + SHelper.getShortString(mCalendar));/* + "\n" +
                mDay + "\n" +
                ((mDay != null) ? mDay.getPeriods(mGroupN).size() : "null") + " periods" +
                "\nGroup number " + mGroupN);*/
        return v;
    }

    private class ScheduleAdapter extends ArrayAdapter<Period> {
        private final Context mContext;

        public ScheduleAdapter(Context context, List<Period> periods) {
            super(context, R.layout.layout_period);
            mContext = context;

            clear();
            addAll(periods);
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            // Layout
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.layout_period, parent,
                    false);
            TextView shortNameView = (TextView) rowView.findViewById(R.id.shortName);
            TextView classNameView = (TextView) rowView.findViewById(R.id.className);
            TextView startTimeView = (TextView) rowView.findViewById(R.id.startTime);
            TextView endTimeView = (TextView) rowView.findViewById(R.id.endTime);

            //TextView[] allTextViews = {shortNameView, classNameView, startTimeView, endTimeView};

            // Data
            Period p = getItem(pos);
            shortNameView.setText(p.getShort());
            classNameView.setText(p.getClassName());

            // Times
            boolean is24hr = false; //PreferenceManager.getDefaultSharedPreferences(SActivity.this).getBoolean(SettingsFragment.IS24HR_KEY, true);
            startTimeView.setText(p.getTimeAsString(Period.TimeStyle.START, is24hr));
            endTimeView.setText(p.getTimeAsString(Period.TimeStyle.END, is24hr));

            // Holiday coloring
            /*if (mSchedule.getHoliday(mSchedule.getTodayAsCalendar()) != null) {
                SHelper.setTextColor(getResources().getColor(R.color.gold), allTextViews);
            } else {
                setRelativeColors(p, allTextViews);
            }*/

            // Note
            if (p.getNote() != null)
                SHelper.setTextColor(getResources().getColor(android.R.color.holo_blue_bright),
                        shortNameView, classNameView);

            return rowView;
        }
    }
}
