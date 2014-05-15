/**
 * ScheduleActivity.java
 * 11 May 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import com.sarangjoshi.rhsmustangs.R;

public class ScheduleActivity extends Activity {

	private ListView periodList;

	public ArrayList<Period> periods;

	private PeriodsAdapter periodsAdapter;
	private ScheduleParser sp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);

		sp = new ScheduleParser();
	}

	@Override
	public void onStart() {
		super.onStart();

		periodList = (ListView) findViewById(R.id.periodsListView);
		// loadPeriods();
		periods = sp.getPeriods();
		loadPeriods();
	}

	/**
	 * Preset schedule, for testing.
	 */
	@SuppressWarnings({ "deprecation", "unused" })
	private void setPeriods() {
		periods = new ArrayList<Period>();

		periods.add(new Period("P1", "Sp 3", new Time(7, 30, 0), new Time(8,
				24, 0)));
		periods.add(new Period("P2", "AP Lit", new Time(8, 30, 0), new Time(9,
				24, 0)));
		periods.add(new Period("P3", "AP Gov", new Time(9, 30, 0), new Time(10,
				24, 0)));
		periods.add(new Period("P4", "Lead", new Time(10, 30, 0), new Time(11,
				24, 0)));
		periods.add(new Period("LC", "LUNCH", new Time(11, 30, 0), new Time(12,
				0, 0)));
		periods.add(new Period("P5", "CSE", new Time(12, 06, 0), new Time(1, 0,
				0)));
		periods.add(new Period("P6", "AP Calc", new Time(1, 06, 0), new Time(2,
				0, 0)));

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
			startTimeView.setText(p.getStartTime());
			endTimeView.setText(p.getEndTime());

			return rowView;
		}
	}
}
