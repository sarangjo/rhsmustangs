/**
 * ScheduleActivity.java
 * 11 May 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sarangjoshi.rhsmustangs.R;

public class SActivity extends FragmentActivity implements
		EditPDFragment.EditPeriodDialogListener {

	private ListView periodList;
	ImageButton nextDay, previousDay;

	public ArrayList<Period> periods;

	private PeriodsAdapter periodsAdapter;
	private SParser mParser;

	public static enum PeriodTime {
		PAST, PRESENT, FUTURE
	}

	private int chosenIndex = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule2);

		mParser = new SParser(this);

		periodList = (ListView) findViewById(R.id.periodsListView);
		periodList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int pos, long id) {
				// Sets the local variable to the currently selected list index
				chosenIndex = pos;
				// Initializes DialogFragment and sets the default text
				EditPDFragment dialog = new EditPDFragment();
				dialog.hintText = periods.get(pos).mClassName;
				dialog.show(getSupportFragmentManager(),
						"EditPeriodDialogFragment");
				return true;
			}

		});

		// Sets up the click listeners of the next and previous day buttons
		setupNextPrev();
		
		// Initially, shows current schedule
		SStaticData.updateCurrentTime();
		mParser.updateScheduleDay(SStaticData.now, true);
	}

	private void setupNextPrev() {
		previousDay = (ImageButton) findViewById(R.id.previousDay);
		nextDay = (ImageButton) findViewById(R.id.nextDay);

		previousDay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				changeSchedule(-1);
			}
		});
		nextDay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				changeSchedule(1);
			}
		});

	}
	

	/**
	 * Changes the schedule by the given number of days.
	 * 
	 * @param d
	 *            the number of days forwards or backwards to change schedule by
	 */
	public void changeSchedule(int d) {
		// Step one is to move the scheduleDay according to d
		mParser.shiftDay(d);
		// Now that scheduleDay is updated, the 
		updatePeriods();
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

		// The third parameter is defined for the selected view (default)
		ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter
				.createFromResource(this, R.array.lunches_list,
						R.layout.lunch_dropdown_default);
		// This is for all the drop down resources
		spinAdapter.setDropDownViewResource(R.layout.lunch_dropdown_item);
		spin.setAdapter(spinAdapter);

		spin.setOnItemSelectedListener(new LunchSelectedListener());

		int pos = spinAdapter.getPosition(mParser.getLunchForAdapter());
		spin.setSelection(pos);

		return super.onCreateOptionsMenu(menu);
	}

	private class LunchSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			mParser.lunchSelected(position);
			updatePeriods();
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
			updatePeriods();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Loads the current day's periods into the adapter and attaches the adapter to the
	 * ListView.
	 *
	 */
	private void updatePeriods() {
		// Gets the periods from parser
		periods = mParser.getPeriods();

		// Sets the title depending on the current day
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(mParser.getScheduleTitle());

		// Loads adapter
		if (periods != null) {
			periodsAdapter = new PeriodsAdapter(this, periods);
			periodList.setAdapter(periodsAdapter);
		}
	}

	private void setColor(int c, TextView... views) {
		for (TextView v : views) {
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

			periodNumView.setText(new String(p.mPeriodShort));
			classNameView.setText(p.mClassName);
			startTimeView.setText(p.getStartTimeAsString());
			endTimeView.setText(p.getEndTimeAsString());

			PeriodTime relTime = getPeriodRelativeTime(p);

			// Colors stuff based on time
			if (relTime == PeriodTime.PAST) {
				periodNumView.setTextColor(Color.GRAY);
			} else if (relTime == PeriodTime.PRESENT) {
				int gold = Color.rgb(255, 215, 0);
				setColor(gold, periodNumView, classNameView, startTimeView,
						endTimeView);
			} else if (relTime == PeriodTime.FUTURE) {
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
		private PeriodTime getPeriodRelativeTime(Period p) {
			SStaticData.updateCurrentTime();
			ScheduleTime schedNow = SStaticData.getCurrentScheduleTime();
			int day = SStaticData.now.weekDay;

			if (day != Time.SATURDAY && day != Time.SUNDAY) {
				if (schedNow.isAfter(p.mEndTime)) {
					return PeriodTime.PAST;
				} else if (schedNow.isAfter(p.mStartTime)
						&& schedNow.isBefore(p.mEndTime)) {
					return PeriodTime.PRESENT;
				} else {
					return PeriodTime.FUTURE;
				}
			}
			return PeriodTime.PAST;
		}
	}

	// NAME CHANGE DIALOGS
	@Override
	public void onDialogPositiveClick(DialogFragment dialog, String savedName) {
		Period p = periods.get(chosenIndex);
		// Setting the period's class name
		p.mClassName = savedName;
		mParser.getSData().setPeriodName(p, savedName);
		updatePeriods();
	}

	@Override
	public void onDialogNeutralClick(DialogFragment dialog) {
		Period p = periods.get(chosenIndex);
		p.mClassName = periods.get(chosenIndex).getDefaultPeriodName();
		mParser.getSData().deletePeriodName(p);
		updatePeriods();
	}

}