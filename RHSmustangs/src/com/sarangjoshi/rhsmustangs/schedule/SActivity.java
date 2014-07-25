/**
 * SActivity.java
 * 11 May 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sarangjoshi.rhsmustangs.MainActivity;
import com.sarangjoshi.rhsmustangs.Network;
import com.sarangjoshi.rhsmustangs.OnSwipeListener;
import com.sarangjoshi.rhsmustangs.R;

public class SActivity extends FragmentActivity implements
		EditPeriodFragment.EditPeriodDialogListener {

	/**
	 * The state of the Schedule
	 * 
	 * @author Sarang
	 */
	private enum ScheduleState {
		INIT, DEFAULT
	}

	ScheduleState mSState;

	ListView periodList;
	ImageButton nextDay, previousDay;
	TextView scheduleTitle, scheduleWeekDay;
	LinearLayout scheduleLayout;

	public ArrayList<Period> periods;

	private PeriodsAdapter periodsAdapter;
	private SParser mParser;

	// The index of the periods that has currently been selected
	private int chosenIndex = -1;

	public static final int PICK_SCHEDULE_REQUEST = 0;
	public static final String SCHEDULE_INDEX_KEY = "si_key";

	public static final String INIT_KEY = "just-init";

	private class MySwipeListener extends OnSwipeListener {
		public MySwipeListener() {
			super(SActivity.this);
		}

		@Override
		public void onSwipeRight() {
			changeSchedule(-1);
			updatePeriods();
			// Toast.makeText(SActivity.this, "Swiped right.",
			// Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onSwipeLeft() {
			changeSchedule(1);
			updatePeriods();
			// Toast.makeText(SActivity.this, "Swiped left.",
			// Toast.LENGTH_SHORT).show();
		}
	}

	// ACTIVITY BASE METHODS
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mParser = new SParser(this);

		if (!mParser.getSData().getIsInitialized()) {
			// INITIALIZATION
			mSState = ScheduleState.INIT;
			updateLayout();
			new InitializeScheduleTask(this).execute();
		} else {
			if (getIntent().getBooleanExtra(INIT_KEY, false))
				Toast.makeText(this, "Schedule initialized. You're all set!",
						Toast.LENGTH_LONG).show();

			mSState = ScheduleState.DEFAULT;
			updateLayout();

			mParser.parseUpdatesFile();

			scheduleLayout = (LinearLayout) findViewById(R.id.scheduleLayout);
			scheduleLayout.setOnTouchListener(new MySwipeListener());

			periodList = (ListView) findViewById(R.id.periodsListView);
			periodList
					.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View v, int pos, long id) {
							// Sets the local variable to the currently selected
							// list index
							chosenIndex = pos;
							Period p = periods.get(pos);
							if (p.isCustomizable) {
								// Initializes DialogFragment and sets the
								// default text
								EditPeriodFragment dialog = new EditPeriodFragment();
								dialog.hintText = p.mClassName;
								dialog.show(getSupportFragmentManager(),
										"EditPeriodDialogFragment");

								imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.toggleSoftInput(
										InputMethodManager.SHOW_FORCED, 0);
							}
							return true;
						}

					});
			periodList.setOnTouchListener(new MySwipeListener());

			// Sets up the click listeners of the next and previous day buttons
			previousDay = (ImageButton) findViewById(R.id.previousDay);
			nextDay = (ImageButton) findViewById(R.id.nextDay);
			scheduleTitle = (TextView) findViewById(R.id.title);
			scheduleWeekDay = (TextView) findViewById(R.id.scheduleDay);

			previousDay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					changeSchedule(-1);
					updatePeriods();
				}
			});
			nextDay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					changeSchedule(1);
					updatePeriods();
				}
			});
			scheduleTitle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SStaticData.updateCurrentTime();
					mParser.updateScheduleDay(SStaticData.now, true);
					updatePeriods();
				}

			});
			scheduleWeekDay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SStaticData.updateCurrentTime();
					mParser.updateScheduleDay(SStaticData.now, true);
					updatePeriods();
				}

			});

			/*
			 * View overlay = new View(this); WindowManager.LayoutParams p = new
			 * WindowManager.LayoutParams(); p.gravity = Gravity.TOP; p.type =
			 * WindowManager.LayoutParams.TYPE_APPLICATION_PANEL; p.token =
			 * overlay.getWindowToken(); overlay.setOnTouchListener(new
			 * MySwipeListener()); WindowManager wManager = (WindowManager)
			 * getSystemService(Context.WINDOW_SERVICE);
			 * wManager.addView(overlay, p);
			 */
			// Initially, shows current schedule
			SStaticData.updateCurrentTime();
			mParser.updateScheduleDay(SStaticData.now, true);

			new LoadScheduleTask().execute();

			// Service
			boolean ua = getIntent().getBooleanExtra(
					SService.UPDATES_AVAILABLE_KEY, false);

			stopAlarm();

			if (ua)
				new DownloadScheduleTask().execute();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		startAlarm();
	}

	@Override
	public void onActivityResult(int request, int result, Intent data) {
		if (request == PICK_SCHEDULE_REQUEST) {
			if (result == RESULT_OK) {
				// we're good.
				int n = data.getIntExtra(SCHEDULE_INDEX_KEY, -1);
				if (n >= 0) {
					mParser.setAltDay(n - 1);
					new LoadScheduleTask().execute();
				}
			}
		}
	}

	// SERVICE
	AlarmManager alarmManager;
	PendingIntent operation;
	int timeInSec = 60 * 30;

	public void startAlarm() {
		Calendar cal = Calendar.getInstance();

		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.HOUR_OF_DAY, 16);
		cal.set(Calendar.MINUTE, 0);

		Intent broadcastIntent = new Intent(this, AlarmReceiver.class);
		operation = PendingIntent.getBroadcast(this, 0, broadcastIntent, 0);

		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				cal.getTimeInMillis(), 1000 * timeInSec, operation);

		// alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
		// cal.getTimeInMillis(), timeInSec * 1000, operation);
		// alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
		// + (10 * 1000), operation);

		// Toast.makeText(this, "Alarm started; " + timeInSec + " seconds",
		// Toast.LENGTH_SHORT).show();
	}

	public void stopAlarm() {
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.cancel(operation);

		// Toast.makeText(this, "Alarm stopped.", Toast.LENGTH_SHORT).show();
	}

	// ACTION BAR
	// Really only to inflate the spinner programmatically.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (mSState == ScheduleState.DEFAULT) {
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

			int pos = spinAdapter.getPosition(mParser.getSpinnerLunch());
			spin.setSelection(pos);

			return super.onCreateOptionsMenu(menu);
		} else
			return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh_schedule:
			new DownloadScheduleTask().execute();
			return true;
		case R.id.action_resetperiods:
			if (mParser.getSData().resetPeriods())
				Toast.makeText(this, "Reset periods.", Toast.LENGTH_LONG)
						.show();
			updatePeriods();
			return true;
		case R.id.action_updatedDays:
			// if (mParser.getAlteredDays() == null)
			// new DownloadScheduleTask().execute();
			showAlteredDays();
			return true;
			/*
			 * case R.id.action_deleteupdates: if (mParser.deleteUpdates()) {
			 * Toast.makeText(this, "Deleted updates.", Toast.LENGTH_LONG)
			 * .show(); updatePeriods(); } return true;
			 */
		case R.id.action_downloadBase:
			new DownloadBaseScheduleTask().execute();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void showAlteredDays() {
		SelectUpdDayFragment dialog = new SelectUpdDayFragment();
		dialog.setUpdatedDays(mParser.getUpdatedDays());
		dialog.show(getSupportFragmentManager(), "SelectAltDayFragment");
	}

	// LUNCH SPINNER
	private class LunchSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			mParser.lunchSelected(position);
			updatePeriods();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// do nothing
		}
	}

	// LOADING PERIODS
	/**
	 * Loads the current day's periods into the adapter and attaches the adapter
	 * to the ListView.
	 */
	private void updatePeriods() {
		// Gets the periods from parser

		periods = mParser.getPeriods();

		// Sets the title depending on the current day
		scheduleTitle = (TextView) findViewById(R.id.title);
		scheduleTitle.setText(mParser.getScheduleTitle());

		scheduleWeekDay = (TextView) findViewById(R.id.scheduleDay);
		scheduleWeekDay.setText(SStaticData.getDay(mParser.getScheduleDay()));

		// Sets color based on if the schedule is adjusted or not
		if (mParser.isScheduleAdjusted()) {
			this.setTextColor(Color.parseColor("#006600"), scheduleTitle,
					scheduleWeekDay);
			scheduleTitle.setTypeface(null, Typeface.BOLD);
		} else {
			this.setTextColor(Color.BLACK, scheduleTitle, scheduleWeekDay);
			scheduleTitle.setTypeface(null, Typeface.NORMAL);
		}

		// Loads adapter
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

			periodNumView.setText(new String(p.mPeriodShort));
			classNameView.setText(p.mClassName);
			startTimeView.setText(p.getStartTimeAsString());
			endTimeView.setText(p.getEndTimeAsString());

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
		private int getPeriodRelativeTime(Period p) {
			SStaticData.updateCurrentTime();
			STime schedNow = SStaticData.getCurrentScheduleTime();
			int day = mParser.getScheduleDay().weekDay;
			int julian = SStaticData.getJulianDay(mParser.getScheduleDay())
					- SStaticData.getJulianDay(SStaticData.now);

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

	// NAME CHANGE DIALOGS
	InputMethodManager imm;

	@Override
	public void onDialogPositiveClick(DialogFragment dialog, String savedName) {
		closeKeyboard(dialog);
		Period p = periods.get(chosenIndex);
		// Setting the period's class name
		p.mClassName = savedName;
		mParser.getSData().setPeriodName(p, savedName);
		updatePeriods();
	}

	@Override
	public void onDialogNeutralClick(DialogFragment dialog) {
		closeKeyboard(dialog);
		Period p = periods.get(chosenIndex);
		p.mClassName = periods.get(chosenIndex).getDefaultPeriodName();
		mParser.getSData().deletePeriodName(p);
		updatePeriods();
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		closeKeyboard(dialog);
	}

	private void closeKeyboard(DialogFragment d) {
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromInputMethod(((EditPeriodFragment)
		// d).edit.getWindowToken(), 0);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}

	// ASYNCTASKS
	/**
	 * Initializes the schedule.
	 * 
	 * Return types: <br>
	 * <ul>
	 * <li>"Y" Success</li>
	 * <li>"I" Internet connection error</li>
	 * <li>"E" General error</li>
	 * </ul>
	 * 
	 * @author Sarang
	 */
	private class InitializeScheduleTask extends AsyncTask<Void, Void, String> {
		ProgressDialog pd;
		Context mCtx;

		public InitializeScheduleTask(Context ctx) {
			mCtx = ctx;
		}

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(mCtx, "",
					"Initializing schedule...\nThis may take a while.");
		}

		@Override
		protected String doInBackground(Void... params) {
			if (Network.isConnectedToInternet(SActivity.this))
				if (mParser.downloadBaseSchedules())
					return "Y";
				else {
					return "E";
				}
			return "I";
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("Y")) { // success
				mParser.getSData().saveInitialize(true);
				// Toast.makeText(mCtx, "Schedule initialized!",
				// Toast.LENGTH_LONG).show();
				// setupSchedule();
				// recreate();
				Intent i = new Intent(mCtx, SActivity.class);
				i.putExtra(INIT_KEY, true);
				finish();
				mCtx.startActivity(i);
			} else {
				mParser.getSData().saveInitialize(false);
				if (result.equals("I"))
					// Internet error
					setMessage("Please check your connection to the Internet.");
				else
					// general error
					setMessage("There was an error. Please try again later or contact the ASB PR.");
			}

			pd.dismiss();
		}

	}

	private class LoadScheduleTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;

		protected void onPreExecute() {
			pd = ProgressDialog.show(SActivity.this, "", "Loading schedule...");
		}

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}

		protected void onPostExecute(Void result) {
			updatePeriods();
			pd.dismiss();
		}
	}

	/**
	 * Downloads the schedule from the online file.
	 * 
	 * @author Sarang
	 */
	private class DownloadScheduleTask extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(SActivity.this, "",
					"Checking for updates...");
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (!Network.isConnectedToInternet(SActivity.this)) {
				Toast.makeText(SActivity.this,
						"No connection to the Internet.", Toast.LENGTH_LONG)
						.show();
				return false;
			}

			if (mParser.saveUpdatesFile()) {
				mParser.parseUpdatesFile();
				return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			Toast t;
			if (result) {
				updatePeriods();
				t = Toast.makeText(SActivity.this,
						"Schedule has been updated!", Toast.LENGTH_LONG);
				mParser.getSData().saveNotification(false);
				showAlteredDays();
			} else {
				t = Toast.makeText(SActivity.this, "No updates!",
						Toast.LENGTH_LONG);
			}
			pd.dismiss();
			t.show();
		}
	}

	private class DownloadBaseScheduleTask extends
			AsyncTask<Void, Void, Boolean> {
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(SActivity.this, "", "Downloading base...");
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (!Network.isConnectedToInternet(SActivity.this)) {
				Toast.makeText(SActivity.this,
						"No connection to the Internet.", Toast.LENGTH_LONG)
						.show();
				return false;
			}
			mParser.downloadBaseSchedules();

			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			pd.dismiss();
		}
	}

	// HELPERS
	/**
	 * Changes the schedule by the given number of days.
	 * 
	 * @param d
	 *            the number of days forwards or backwards to change schedule by
	 */
	public void changeSchedule(int d) {
		// Step one is to move the scheduleDay according to d
		mParser.shiftDay(d);
		// Now that scheduleDay is updated, the periods can be updated
		// updatePeriods();
	}

	public void setMessage(String s) {
		if (mSState == ScheduleState.INIT) {
			TextView v = (TextView) findViewById(R.id.messageView);
			v.setText(s);
		}
	}

	private void updateLayout() {
		switch (mSState) {
		case DEFAULT:
			setContentView(R.layout.activity_schedule2);
			break;
		case INIT:
			setContentView(R.layout.activity_schedule);
			break;
		default:
			setContentView(R.layout.activity_schedule2);
			break;
		}
	}

	/**
	 * Sets the text color of the given views.
	 * 
	 */
	private void setTextColor(int c, TextView... views) {
		for (TextView v : views) {
			v.setTextColor(c);
		}
	}
}