package com.sarangjoshi.rhsmustangs.schedule;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sarangjoshi.rhsmustangs.R;
import com.sarangjoshi.rhsmustangs.content.SDay;
import com.sarangjoshi.rhsmustangs.content.SPeriod;
import com.sarangjoshi.rhsmustangs.content.SSchedule;

/**
 * Created by Sarang on 4/8/2015.
 */
public class ScheduleFragment extends Fragment {
    private SSchedule mBaseSchedule;

    private ScheduleAdapter mAdapter;

    private ListView mPeriodsList;

    private Time mToday;

    /**
     * Default empty constructor.
     */
    public ScheduleFragment() {
        mBaseSchedule = new SSchedule();
        mBaseSchedule.loadDefaultSchedule();
        mToday = new Time();
        mToday.setToNow();
    }

    /**
     * Initializes a new instance of ScheduleFragment.
     */
    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ScheduleAdapter(getActivity(), mBaseSchedule.getDay(mToday.weekDay));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_schedule, container, false);

        // Set up adapter
        mPeriodsList = (ListView) v.findViewById(R.id.periodsListView);
        mPeriodsList.setAdapter(mAdapter);

        return v;
    }

    /**
     * Sets the text color of the given views.
     *
     * @param c the color
     */
    private void setTextColor(int c, TextView... views) {
        for (TextView v : views) {
            v.setTextColor(c);
        }
    }

    /**
     * Private class to adapt a list of periods.
     */
    private class ScheduleAdapter extends ArrayAdapter<SPeriod> {
        private final Context mContext;
        private SDay mDay;

        public ScheduleAdapter(Context context, SDay day) {
            super(context, R.layout.layout_period, day.getPeriods());
            mContext = context;
            mDay = day;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView;
            TextView periodNumView, classNameView, startTimeView, endTimeView;

            // Holiday?
            /*if (mIsU == SParser.UPDATED_HOL) {
                rowView = inflater.inflate(R.layout.layout_hol_period, parent,
                        false);

                // Individual views
                periodNumView = (TextView) rowView
                        .findViewById(R.id.periodNumHol);
                classNameView = (TextView) rowView
                        .findViewById(R.id.classNameHol);
                startTimeView = (TextView) rowView
                        .findViewById(R.id.startTimeHol);
                endTimeView = (TextView) rowView.findViewById(R.id.endTimeHol);
            } else {*/
            rowView = inflater.inflate(R.layout.layout_period, parent,
                    false);

            // Individual views
            periodNumView = (TextView) rowView.findViewById(R.id.periodNum);
            classNameView = (TextView) rowView.findViewById(R.id.className);
            startTimeView = (TextView) rowView.findViewById(R.id.startTime);
            endTimeView = (TextView) rowView.findViewById(R.id.endTime);

            //}

            // Setting view data
            SPeriod p = mDay.getPeriod(pos);

            periodNumView.setText(new String(p.mPeriodShort));
            classNameView.setText(p.mClassName);

            boolean is24hr = true; //PreferenceManager.getDefaultSharedPreferences(SActivity.this).getBoolean(SettingsFragment.IS24HR_KEY,                    true);
            startTimeView.setText(p.getTimeAsString(SPeriod.TimeStyle.START, is24hr));
            endTimeView.setText(p.getTimeAsString(SPeriod.TimeStyle.END, is24hr));

            int relTime = getPeriodRelativeTime(p);

            // Colors stuff based on time
            if (relTime < 0) {
                periodNumView.setTextColor(Color.GRAY);
            } else if (relTime == 0) {
                int gold = Color.rgb(255, 215, 0);
                setTextColor(gold, periodNumView, classNameView, startTimeView,
                        endTimeView);
            } else if (relTime > 0) {
                periodNumView.setTextColor(Color.BLACK);
            }

            return rowView;
        }

        /**
         * Given the time and current time, gets the relative time style.
         *
         * @param p
         *            the chosen period
         */
        private int getPeriodRelativeTime(SPeriod p) {
            SStatic.updateCurrentTime();
            SPeriod.STime schedNow = SStatic.getCurrentScheduleTime();
            int day = mDay.getDayOfWeek();
            int julian = SStatic.getJulianDay(mToday) - SStatic.getJulianDay(SStatic.now);
            if (julian != 0) {
                // Past day
                return julian;
            } else {
                // Present day
                if (day != Time.SATURDAY && day != Time.SUNDAY) {
                    if (schedNow.isAfter(p.mEndTime)) {
                        return -1;
                    } else if (schedNow.isAfter(p.mStartTime)
                            && schedNow.isBefore(p.mEndTime)) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
                return -1;
            }
        }
    }

}

