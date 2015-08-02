package com.sarangjoshi.rhsmustangs.schedule;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sarangjoshi.rhsmustangs.R;
import com.sarangjoshi.rhsmustangs.content.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * Created by Sarang on 4/8/2015.
 */
public class ScheduleFragment extends Fragment implements SSchedule.UpdateFinishedListener,
        UpdatedDaysFragment.UpdatedDaySelectedListener {
    private static final String UPDATED_DAYS_TAG = "UpdatedDaysFragment";

    private SSchedule mSchedule;

    private ScheduleAdapter mAdapter;

    private ListView mPeriodsList;
    private TextView mTitle, mDayOfWeek;
    private ImageButton mPrevDay, mNextDay;
    private Spinner groupSpin;

    private ProgressDialog dialog;

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

        mSchedule = new SSchedule(new GregorianCalendar(), 1, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_schedule, container, false);

        // Initialize views
        mPeriodsList = (ListView) v.findViewById(R.id.periodsListView);
        mTitle = (TextView) v.findViewById(R.id.title);
        mDayOfWeek = (TextView) v.findViewById(R.id.scheduleDayOfWeek);
        mPrevDay = (ImageButton) v.findViewById(R.id.previousDay);
        mNextDay = (ImageButton) v.findViewById(R.id.nextDay);

        // UI dynamic setup
        View.OnClickListener dcl = new DayChangeClickListener();
        mPrevDay.setOnClickListener(dcl);
        mNextDay.setOnClickListener(dcl);

        View.OnClickListener tcl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToday(new GregorianCalendar());
            }
        };
        mTitle.setOnClickListener(tcl);
        mDayOfWeek.setOnClickListener(tcl);

        // And finally actually show data
        refreshPeriods();
        updateSpinner();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.schedule_menu, menu);

        MenuItem item = menu.findItem(R.id.group_spinner);
        groupSpin = (Spinner) MenuItemCompat.getActionView(item);

        updateSpinner();
    }

    /**
     * Whether the selection is handled.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                dialog = ProgressDialog.show(getActivity(), "",
                        "Checking for updates...");

                mSchedule.updateUpdatedDays();
                return true;
            case R.id.action_see_updated_days:
                UpdatedDaysFragment dialog =
                        new UpdatedDaysFragment(mSchedule.getUpdatedDays(), this);
                dialog.show(getFragmentManager(), UPDATED_DAYS_TAG);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Updates the spinner.
     */
    private void updateSpinner() {
        if (groupSpin != null) {
            // Retrieve data
            String[] spinnerData = mSchedule.getToday().getGroupNames();

            ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_dropdown_default, spinnerData);
            spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            groupSpin.setAdapter(spinAdapter);

            groupSpin.setOnItemSelectedListener(new GroupSpinnerListener());

            if (mSchedule.getToday().hasGroups())
                groupSpin.setSelection(mSchedule.getGroupN() - 1);
            else
                groupSpin.setSelection(0);
        }
    }

    @Override
    public void updateCompleted() {
        dialog.dismiss();
        refreshPeriods();
        updateSpinner();
    }

    @Override
    public void updatedDaySelected(int index) {
        Toast.makeText(getActivity(), "" + index, Toast.LENGTH_SHORT).show();

        SUpdatedDay day = mSchedule.getUpdatedDays().get(index);
        setToday(day.getDate());
    }

    private void setToday(Calendar today) {
        mSchedule.setToday(today);
        refreshPeriods();
        updateSpinner();
    }

    private class GroupSpinnerListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (mSchedule.setGroupN(position + 1)) {
                Toast.makeText(getActivity(), "" + (position + 1), Toast.LENGTH_SHORT).show();
                refreshPeriods();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // do nothing
        }
    }

    /**
     * Refreshes the periods.
     */
    private void refreshPeriods() {
        // Updates adapter to reflect changes
        if (mAdapter == null)
            mAdapter = new ScheduleAdapter(getActivity());

        mAdapter.updateData();
        mPeriodsList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        // Update other UI
        updateTitle();
    }

    /**
     * Updates the title of the current schedule.
     */
    private void updateTitle() {
        mTitle.setText(mSchedule.getTodayAsString());
        mDayOfWeek.setText(mSchedule.getTodayDayOfWeekAsString());
    }

    private class DayChangeClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Actual shifting - result can be captured to note week shift
            mSchedule.shiftTodayBy((v.getId() == mNextDay.getId()) ? 1 : -1);

            // Updating
            refreshPeriods();
            updateSpinner();
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
            super(context, R.layout.layout_period);
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
            SPeriod p = getItem(pos);

            periodNumView.setText(new String(p.getShort()));
            classNameView.setText(p.getClassName());

            boolean is24hr = false; //PreferenceManager.getDefaultSharedPreferences(SActivity.this).getBoolean(SettingsFragment.IS24HR_KEY,                    true);
            startTimeView.setText(p.getTimeAsString(SPeriod.TimeStyle.START, is24hr));
            endTimeView.setText(p.getTimeAsString(SPeriod.TimeStyle.END, is24hr));

            int relTime = getPeriodRelativeTime(p);

            // Colors stuff based on time
            int color = Color.GRAY; // relTime < 0
            if (relTime == 0) {
                color = Color.rgb(255, 215, 0);
            } else if (relTime > 0) {
                color = Color.BLACK;
            }
            setTextColor(color, periodNumView, classNameView, startTimeView, endTimeView);

            return rowView;
        }

        public void updateData() {
            super.clear();
            super.addAll(mSchedule.getTodayPeriods());
        }

        // TODO: Clean this the hell up

        /**
         * Given the time and current time, gets the relative time style.
         *
         * @param p the chosen period
         */
        private int getPeriodRelativeTime(SPeriod p) {
            SPeriod.STime schedNow = new SPeriod.STime(new GregorianCalendar());
            int day = mSchedule.getToday().getDayOfWeek();
            int absDiff = SStatic.getAbsDifference(mSchedule.getTodayAsCalendar(), new GregorianCalendar());
            if (absDiff != 0)
                return absDiff;
            // Present day
            if (day != Calendar.SATURDAY && day != Calendar.SUNDAY) {
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
