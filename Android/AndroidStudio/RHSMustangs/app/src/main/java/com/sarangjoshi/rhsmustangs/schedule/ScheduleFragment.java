package com.sarangjoshi.rhsmustangs.schedule;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sarangjoshi.rhsmustangs.R;
import com.sarangjoshi.rhsmustangs.content.*;

/**
 * Created by Sarang on 4/8/2015.
 */
public class ScheduleFragment extends Fragment {
    private SSchedule mSchedule;

    private ScheduleAdapter mAdapter;

    private ListView mPeriodsList;
    private TextView mTitle, mDayOfWeek;
    private ImageButton mPrevDay, mNextDay;
    private Spinner groupSpin;

    /**
     * Default empty constructor.
     */
    public ScheduleFragment() {
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
        setHasOptionsMenu(true);

        Time today = new Time();
        today.setToNow();
        mSchedule = new SSchedule(SWeek.getDefaultWeek(), today, 1);
        mAdapter = new ScheduleAdapter(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.schedule_menu, menu);

        MenuItem spinner = menu.findItem(R.id.group_spinner);
        groupSpin = (Spinner) spinner.getActionView();
        if (groupSpin != null) {
            groupSpin.setVisibility(View.VISIBLE);

            String[] spinnerData = new String[]{"O", "G"};

            // The third parameter is defined for the selected view
            // (default)
            ArrayAdapter<String> spinAdapter = //ArrayAdapter.createFromResource(getActivity(), R.array.groups_array, R.layout.spinner_dropdown_default);
                    new ArrayAdapter<>(getActivity(), R.layout.spinner_dropdown_default, spinnerData);

            // This is for all the drop down resources
            spinAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            groupSpin.setAdapter(spinAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_switch:
                mSchedule.setGroupN((mSchedule.getGroupN() == 2) ? 1 : 2);
                refreshList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Refreshes the title and list.
     */
    private void refreshList() {
        updateTitle();

        // Updates adapter to reflect changes
        mAdapter = new ScheduleAdapter(getActivity());
        mPeriodsList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_schedule, container, false);

        // Set up adapter
        mPeriodsList = (ListView) v.findViewById(R.id.periodsListView);
        mPeriodsList.setAdapter(mAdapter);

        // Other views in the schedule
        mTitle = (TextView) v.findViewById(R.id.title);
        mDayOfWeek = (TextView) v.findViewById(R.id.scheduleDayOfWeek);
        mPrevDay = (ImageButton) v.findViewById(R.id.previousDay);
        mNextDay = (ImageButton) v.findViewById(R.id.nextDay);

        updateTitle();

        View.OnClickListener l = new DayChangeClickListener();
        mPrevDay.setOnClickListener(l);
        mNextDay.setOnClickListener(l);

        return v;
    }

    /**
     * Updates the title of the current schedule.
     */
    private void updateTitle() {
        mTitle.setText(mSchedule.getTodayAsTime().format3339(true));
        mDayOfWeek.setText(mSchedule.getToday().getDayOfWeekAsString());
    }

    private class DayChangeClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Actual shifting
            if (mSchedule.shiftCurrentDayBy1(v.getId() == mNextDay.getId()))
                // debug
                Toast.makeText(getActivity(), "Week shifted.", Toast.LENGTH_SHORT).show();

            refreshList();
        }
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

        public ScheduleAdapter(Context context) {
            super(context, R.layout.layout_period, mSchedule.getTodayPeriods());
            mContext = context;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView;
            TextView periodNumView, classNameView, startTimeView, endTimeView;

            rowView = inflater.inflate(R.layout.layout_period, parent,
                    false);

            // Individual views
            periodNumView = (TextView) rowView.findViewById(R.id.periodNum);
            classNameView = (TextView) rowView.findViewById(R.id.className);
            startTimeView = (TextView) rowView.findViewById(R.id.startTime);
            endTimeView = (TextView) rowView.findViewById(R.id.endTime);

            //}

            // Setting view data
            SPeriod p = mSchedule.getTodayPeriods().get(pos);

            periodNumView.setText(new String(p.getShort()));
            classNameView.setText(p.getClassName());

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

        // TODO: Clean this the hell up

        /**
         * Given the time and current time, gets the relative time style.
         *
         * @param p the chosen period
         */
        private int getPeriodRelativeTime(SPeriod p) {
            SStatic.updateCurrentTime();
            SPeriod.STime schedNow = SStatic.getCurrentScheduleTime();
            int day = mSchedule.getToday().getDayOfWeek();
            int julian = SStatic.getJulianDay(mSchedule.getTodayAsTime()) - SStatic.getJulianDay(SStatic.now);
            if (julian != 0) {
                // Past day
                return julian;
            }
            // Present day
            if (day != Time.SATURDAY && day != Time.SUNDAY) {
                if (p.getEnd().compareTo(schedNow) < 0) {
                    return -1;
                } else if ((p.getStart().compareTo(schedNow) >= 0)
                        && (p.getEnd().compareTo(schedNow) <= 0)) {
                    return 0;
                } else {
                    return 1;
                }
            }
            return -1;
        }
    }
}
