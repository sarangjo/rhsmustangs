/**
 * ScheduleActivity.java
 * 11 May 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.sarangjoshi.rhsmustangs.R;

public class SActivity extends Activity {

	private ListView periodList;

	public ArrayList<Period> periods;

	private PeriodsAdapter periodsAdapter;
	private SParser sParser;
	
	public static enum PeriodStyle {
		PAST, PRESENT, FUTURE
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);

		sParser = new SParser(this);

		periodList = (ListView) findViewById(R.id.periodsListView);

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
		
		View item = menu.findItem(R.id.lunches_spinner).getActionView();
		
		Spinner spin = (Spinner) item;
		
		// The third parameter is defined for the selected view
		ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter.createFromResource(this, R.array.lunches_list, R.layout.lunch_dropdown_default);
		// This is for all the drop down resources
		spinAdapter.setDropDownViewResource(R.layout.lunch_dropdown_item);
		spin.setAdapter(spinAdapter);
		
		spin.setOnItemSelectedListener(new LunchSelectedListener());

		int pos = spinAdapter.getPosition(sParser.getLunchForAdapter());
		spin.setSelection(pos);
		
		return super.onCreateOptionsMenu(menu);
	}

	private class LunchSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position,
				long id) {
			sParser.lunchSelected(position);
			loadPeriods();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// do nothing...?		
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh_schedule:
			periods = sParser.getPeriods();
			loadPeriods();
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
	 * Loads the periods into the adapter.
	 */
	private void loadPeriods() {
		periods = sParser.getPeriods();
		
		if (periods != null) {
			periodsAdapter = new PeriodsAdapter(this, periods);
			periodList.setAdapter(periodsAdapter);
		}
	}

	private void setColor(int c, TextView... views) {
		for(TextView v : views) {
			v.setTextColor(c);
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
				setColor(Color.rgb(255, 215, 0), periodNumView, classNameView);
			} else if (style == PeriodStyle.FUTURE) {
				periodNumView.setTextColor(Color.BLACK);
			}

			return rowView;
		}

		private PeriodStyle getPeriodStyle(Period p) {
			ScheduleTime schedNow = SSData.getCurrentTime();
			int day = SSData.getCurrentDay();

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