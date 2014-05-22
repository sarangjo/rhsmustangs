/**
 * ScheduleActivity.java
 * 11 May 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.sarangjoshi.rhsmustangs.R;

public class ScheduleActivity extends Activity {

	private ListView periodList;

	public ArrayList<Period> periods;

	private PeriodsAdapter periodsAdapter;
	private ScheduleParser sp;
	
	public static enum PeriodStyle {
		PAST, PRESENT, FUTURE
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);

		sp = new ScheduleParser();

		periodList = (ListView) findViewById(R.id.periodsListView);
		// loadPeriods();
		periods = sp.getPeriods();
		loadPeriods();
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.schedule_action_bar, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh_schedule:
			periods = sp.getPeriods();
			loadPeriods();
			return true;
		case R.id.action_change_lunch:
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Preset schedule, for testing.
	 */
	private void setPresetPeriods() {
		periods.add(new Period("P1", "Sp 3", 7, 30, 8, 24));
		periods.add(new Period("P2", "AP Lit", 8, 30, 9, 24));
		periods.add(new Period("P3", "AP Gov", 9, 30, 10, 24));
		periods.add(new Period("P4", "Lead", 10, 30, 11, 24));
		periods.add(new Period("LC", "LUNCH", 11, 30, 12, 0));
		periods.add(new Period("P5", "CSE", 12, 06, 1, 0));
		periods.add(new Period("P6", "AP Calc", 1, 06, 2, 0));
	}

	/**
	 * Actually loads the periods into the adapter.
	 */
	private void loadPeriods() {
		if (periods != null) {
			periodsAdapter = new PeriodsAdapter(this, periods);
			periodList.setAdapter(periodsAdapter);
		}
	}

	private class PeriodsAdapter extends ArrayAdapter<Period> {
		private final Context mContext;

		public PeriodsAdapter(Context context, List<Period> objects) {
			super(context, R.layout.layout_period, objects);

			mContext = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View rowView = inflater.inflate(R.layout.layout_period, parent,
					false);

			// Individual views
			TextView periodNumView = (TextView) rowView
					.findViewById(R.id.periodNum);
			TextView classNameView = (TextView) rowView
					.findViewById(R.id.className);
			TextView startTimeView = (TextView) rowView
					.findViewById(R.id.startTime);
			TextView endTimeView = (TextView) rowView
					.findViewById(R.id.endTime);

			// Setting view data
			Period p = periods.get(position);

			periodNumView.setText(new String(p.mPeriodNum));
			classNameView.setText(p.mClassName);
			startTimeView.setText(p.getStartTimeAsString());
			endTimeView.setText(p.getEndTimeAsString());

			PeriodStyle style = getPeriodStyle(p);
			
			if (style == PeriodStyle.PAST) {
				periodNumView.setTextColor(Color.GRAY);
			} else if (style == PeriodStyle.PRESENT) {
				periodNumView.setTextColor(Color.GREEN);
			} else if (style == PeriodStyle.FUTURE) {
				periodNumView.setTextColor(Color.BLACK);
			}

			return rowView;
		}

		private PeriodStyle getPeriodStyle(Period p) {
			int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
			ScheduleData.now = new Time();
			ScheduleData.now.setToNow();
			
			ScheduleTime schedNow = new ScheduleTime(ScheduleData.now);

			if (day != Calendar.SATURDAY && day != Calendar.SUNDAY) {
				if (schedNow.isAfter(p.mEndTime)) {
					return PeriodStyle.PAST;
				} else if (schedNow.isAfter(p.mStartTime) && schedNow.isBefore(p.mEndTime)) {
					return PeriodStyle.PRESENT;
				} else {
					return PeriodStyle.FUTURE;
				}
			}
			return PeriodStyle.PAST;
		}
	}
}