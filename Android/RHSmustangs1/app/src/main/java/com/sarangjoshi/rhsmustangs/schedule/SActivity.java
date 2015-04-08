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
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sarangjoshi.rhsmustangs.Network;
import com.sarangjoshi.rhsmustangs.OnSwipeListener;
import com.sarangjoshi.rhsmustangs.R;
import com.sarangjoshi.rhsmustangs.SettingsActivity.SettingsFragment;
import com.sarangjoshi.rhsmustangs.schedule.demo.SDemoActivity;
import com.sarangjoshi.rhsmustangs.schedule.fragments.ConfirmResetFragment;
import com.sarangjoshi.rhsmustangs.schedule.fragments.EditPeriodFragment;
import com.sarangjoshi.rhsmustangs.schedule.fragments.LastUpdatesFragment;
import com.sarangjoshi.rhsmustangs.schedule.fragments.SelectUpdDayFragment;

public class SActivity extends FragmentActivity implements
		EditPeriodFragment.EditPeriodDialogListener {

	/**
	 * The state of the Schedule
	 * 
	 * @author Sarang
	 */
	private enum ScheduleState {
		INIT, PERIODS, DEFAULT
	}

	ScheduleState mSState;

	// VIEWS
	ListView periodList;
	ImageButton nextDay, previousDay, nextHol;
	TextView scheduleTitle, scheduleWeekDay;
	Button setPeriodsBtn, skipPeriodsBtn, clearPeriodsBtn;
	Spinner groupSpin;
	Menu actionBar;

	private SParser mParser;

	// The index of the periods that has currently been selected
	private int chosenIndex = -1;

	public static final int PICK_SCHEDULE_REQUEST = 0;
	public static final int RESET_BASE_REQUEST = 1;
	public static final String SCHEDULE_INDEX_KEY = "si_key";

	public static final String INIT_KEY = "just-init";
	public static final String GOTOALTDAY_KEY = "alt-day";

	private class MySwipeListener extends OnSwipeListener {
		public MySwipeListener() {
			super(SActivity.this);
		}

		@Override
		public void onSwipeRight() {
			mParser.shiftDay(-1);
			updatePeriods();
			// Toast.makeText(SActivity.this, "Swiped right.",
			// Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onSwipeLeft() {
			mParser.shiftDay(1);
			updatePeriods();
			// Toast.makeText(SActivity.this, "Swiped left.",
			// Toast.LENGTH_SHORT).show();
		}
	}

	// ACTIVITY CALLBACK METHODS
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mParser = new SParser(this);

		if (!mParser.getSData().getIsInitialized()) {
			goToInit();
		} else {
			PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

			mSState = ScheduleState.DEFAULT;
			updateLayout();

			// Offline operations
			mParser.parseUpdatesFile();
			mParser.readHolidays();

			periodList = (ListView) findViewById(R.id.periodsListView);
			periodList
					.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View v, int pos, long id) {
							// Sets the local variable to the currently selected
							// list index
							chosenIndex = pos;
							SPeriod p = periods.get(pos);
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
							} else
								Toast.makeText(SActivity.this,
										"Not customizable.", Toast.LENGTH_SHORT)
										.show();
							return true;
						}

					});
			periodList.setOnTouchListener(new MySwipeListener());

			previousDay = (ImageButton) findViewById(R.id.previousDay);
			nextDay = (ImageButton) findViewById(R.id.nextDay);
			scheduleTitle = (TextView) findViewById(R.id.title);
			scheduleWeekDay = (TextView) findViewById(R.id.scheduleDay);
			nextHol = (ImageButton) findViewById(R.id.nextHol);
			// Sets up the simple click listeners
			previousDay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					titleButtonClicked(-1);
				}
			});
			nextDay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					titleButtonClicked(1);
				}
			});
			scheduleTitle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					titleButtonClicked(0);
				}

			});
			scheduleWeekDay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					titleButtonClicked(0);
				}

			});
			nextHol.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mParser.goToNextHoliday())
						updatePeriods();
					else
						Toast.makeText(SActivity.this,
								"No holidays in the near future. :(",
								Toast.LENGTH_SHORT).show();
				}
			});
			// Long clicks
			previousDay.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Toast.makeText(SActivity.this, "Previous Day",
							Toast.LENGTH_SHORT).show();
					return true;
				}
			});
			nextDay.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Toast.makeText(SActivity.this, "Next Day",
							Toast.LENGTH_SHORT).show();
					return true;
				}
			});
			scheduleTitle.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Toast.makeText(SActivity.this, "Go to Today",
							Toast.LENGTH_SHORT).show();
					return true;
				}
			});
			scheduleWeekDay.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Toast.makeText(SActivity.this, "Go to Today",
							Toast.LENGTH_SHORT).show();
					return true;
				}
			});
			nextHol.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Toast.makeText(SActivity.this, "Upcoming Holiday",
							Toast.LENGTH_SHORT).show();
					return true;
				}
			});

			SStatic.updateCurrentTime();

			int n = getIntent().getIntExtra(GOTOALTDAY_KEY, -1);
			if (n >= 0) {
				mParser.setAltDay(n - 1);
			} else {
				mParser.setToLatestDay();
				// mParser.updateScheduleDay(SStaticData.now, true);
			}

			updatePeriods();

			// Service
			boolean ua = getIntent().getBooleanExtra(
					SService.UPDATES_AVAILABLE_KEY, false);

			if (getIntent().getBooleanExtra(INIT_KEY, false)) {
				// ua = true;
				Toast.makeText(this, "Base schedule initialized.",
						Toast.LENGTH_SHORT).show();
			}

			if (ua)
				new DownloadScheduleTask().execute();

			if (!mParser.getSData().getDemoWatched()) { // demo hasn't been
														// watched
				mParser.getSData().saveDemoWatched(true);
				startActivity(new Intent(this, SDemoActivity.class));
			}

		}
	}

	@Override
	public void onResume() {
		super.onResume();

		stopAlarm();
		SStatic.updateCurrentTime();
	}

	@Override
	public void onPause() {
		super.onPause();
		// Saving latest day
		if (mSState == ScheduleState.DEFAULT
				|| mSState == ScheduleState.PERIODS)
			saveLatestDay();

		// Alarms
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		if (mParser.getSData().getIsInitialized())
			startAlarm();
	}

	private void saveLatestDay() {
		try {
			String s = mParser.getScheduleDay().toString().substring(0, 8);
			mParser.getSData().saveLatestDay(s);
		} catch (Exception e) {
		}
	}

	@Override
	public void onActivityResult(int request, int result, Intent data) {
		if (result == RESULT_OK) {
			// we're good.
			if (request == PICK_SCHEDULE_REQUEST) {
				int n = data.getIntExtra(SCHEDULE_INDEX_KEY, -1);
				if (n >= 0) {
					// Alt day selected
					if (mSState == ScheduleState.DEFAULT) {
						mParser.setAltDay(n - 1);
						updatePeriods();
					} else {
						Intent i = new Intent(this, SActivity.class);
						i.putExtra(GOTOALTDAY_KEY, n);
						goToDefault(i, false);
					}
				}
			} else if (request == RESET_BASE_REQUEST) {
				resetBase();
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

		Intent broadcastIntent = new Intent(this, SAlarmReceiver.class);
		operation = PendingIntent.getBroadcast(this, 0, broadcastIntent, 0);

		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				cal.getTimeInMillis(), 1000 * timeInSec, operation);

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
		actionBar = menu;
		if (mSState == ScheduleState.DEFAULT) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.schedule_action_bar, actionBar);

			groupSpin = (Spinner) actionBar.findItem(R.id.group_spinner)
					.getActionView();
			if (groupSpin != null)
				groupSpin.setVisibility(View.GONE);

			setSpinnerData();

			return super.onCreateOptionsMenu(actionBar);
		} else
			return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh_schedule:
			SStatic.updateCurrentTime();
			new DownloadScheduleTask().execute();
			return true;
		case R.id.action_updatedDays:
			showAlteredDays();
			return true;
		case R.id.action_setPeriods:
			goToSettingPeriod(false);
			return true;
		case R.id.action_resetEverything:
			// resetEverything();
			showResetConfirm();
			return true;
		case R.id.action_lastUpdates:
			showLastUpdates();
			return true;
		case R.id.action_demo:
			startActivity(new Intent(this, SDemoActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void showResetConfirm() {
		ConfirmResetFragment dialog = new ConfirmResetFragment();
		dialog.show(getSupportFragmentManager(), "ConfirmResetFragment");
	}

	public void resetBase() {
		mParser.resetBase();// resetEverything();
		recreate();
	}

	private void showAlteredDays() {
		SelectUpdDayFragment dialog = new SelectUpdDayFragment();
		dialog.setUpdatedDays(mParser.getUpdatedDays());
		dialog.show(getSupportFragmentManager(), "SelectAltDayFragment");
	}

	private void showLastUpdates() {
		LastUpdatesFragment dialog = new LastUpdatesFragment();
		dialog.schedUpdates = mParser.getSData().getUpdateTime();
		dialog.holUpdates = mParser.getSData().getHolidaysUpdateTime();
		dialog.show(getSupportFragmentManager(), "LastUpdatesFragment");
	}

	@SuppressWarnings("unused")
	private void showConfirmReset() {
		ConfirmResetFragment dialog = new ConfirmResetFragment();
		dialog.show(getSupportFragmentManager(), "ConfirmResetFragment");
	}

	// LOADING PERIODS
	public ArrayList<SPeriod> periods;
	private PeriodsAdapter periodsAdapter;
	String[] spinnerData;

	/**
	 * Loads the current day's periods into the adapter and attaches the adapter
	 * to the ListView.
	 */
	private void updatePeriods() {
		// Gets the periods from parser
		periods = mParser.getPeriods();

		int isU = mParser.getIsUpdated();

		// Sets Spinner data
		setSpinnerData();

		// Sets the title depending on the current day
		scheduleTitle = (TextView) findViewById(R.id.title);
		scheduleTitle.setText(mParser.getScheduleTitle());

		scheduleWeekDay = (TextView) findViewById(R.id.scheduleDay);
		scheduleWeekDay.setText(SStatic.getDay(mParser.getScheduleDay()));

		// Sets color based on if the schedule is adjusted or not
		if (isU != SParser.UPDATED_NO) {
			this.setTextColor(
					Color.parseColor((isU == SParser.UPDATED_HOL) ? SStatic.COLOR_HOLIDAY
							: SStatic.COLOR_UPDATE), scheduleTitle,
					scheduleWeekDay);
			scheduleTitle.setTypeface(null, Typeface.BOLD);
		} else {
			this.setTextColor(Color.BLACK, scheduleTitle, scheduleWeekDay);
			scheduleTitle.setTypeface(null, Typeface.NORMAL);
		}

		// Loads adapter
		if (periods != null) {
			periodsAdapter = new PeriodsAdapter(this, periods, isU);
			periodList.setAdapter(periodsAdapter);
		}

		// Checks for holidays
		if (mParser.isHolidayInFuture() >= 0) {
			nextHol.setColorFilter(Color.parseColor(SStatic.COLOR_HOLIDAY));
		} else {
			nextHol.setColorFilter(Color.TRANSPARENT);
		}
	}

	/**
	 * Sets the spinner data
	 */
	private void setSpinnerData() {
		if (groupSpin != null) {
			String[] oldSpinnerData = spinnerData;
			spinnerData = mParser.getSpinnerValues();
			int pos = mParser.getSelectedGroupN() - 1;// spinAdapter.getPosition(mParser.getSpinnerLunch());

			if (spinnerData == null) {
				// No period groups
				spinnerData = new String[] { "N/A" };
			}

			// If oldSpinnerData is null, this is right after onCreate()
			boolean shouldUpdate = (oldSpinnerData == null);

			if (!shouldUpdate)
				shouldUpdate = mParser.getShouldUpdateSpinner();

			if (shouldUpdate) {
				// The third parameter is defined for the selected view
				// (default)
				ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(
						this, R.layout.spinner_dropdown_default, spinnerData);
				// ArrayAdapter.createFromResource(this, R.array.lunches_list,
				// R.layout.spinner_dropdown_default);
				// This is for all the drop down resources
				spinAdapter
						.setDropDownViewResource(R.layout.spinner_dropdown_item);
				groupSpin.setAdapter(spinAdapter);

				groupSpin
						.setOnItemSelectedListener(new OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> parent,
									View view, int position, long id) {
								mParser.groupSelected(position + 1);
								if (mSState == ScheduleState.DEFAULT)
									updatePeriods();
							}

							@Override
							public void onNothingSelected(AdapterView<?> parent) {
								// do nothing
							}
						});
				groupSpin.setSelection(pos);
			}
		}
	}

	private class PeriodsAdapter extends ArrayAdapter<SPeriod> {
		private final Context mContext;
		private int mIsU;

		public PeriodsAdapter(Context context, List<SPeriod> objects, int isU) {
			super(context, R.layout.layout_period, objects);

			mIsU = isU;
			mContext = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View rowView;
			TextView periodNumView, classNameView, startTimeView, endTimeView;

			// Holiday?
			if (mIsU == SParser.UPDATED_HOL) {
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
			} else {
				rowView = inflater.inflate(R.layout.layout_period, parent,
						false);

				// Individual views
				periodNumView = (TextView) rowView.findViewById(R.id.periodNum);
				classNameView = (TextView) rowView.findViewById(R.id.className);
				startTimeView = (TextView) rowView.findViewById(R.id.startTime);
				endTimeView = (TextView) rowView.findViewById(R.id.endTime);

			}

			// Setting view data
			SPeriod p = periods.get(position);

			periodNumView.setText(new String(p.mPeriodShort));
			classNameView.setText(p.mClassName);

			boolean is24hr = PreferenceManager.getDefaultSharedPreferences(
					SActivity.this).getBoolean(SettingsFragment.IS24HR_KEY,
					true);
			startTimeView.setText(p.getStartTimeAsString(is24hr));
			endTimeView.setText(p.getEndTimeAsString(is24hr));

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
			int day = mParser.getScheduleDay().weekDay;
			int julian = SStatic.getJulianDay(mParser.getScheduleDay())
					- SStatic.getJulianDay(SStatic.now);

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
	public void onDialogPositiveClick(EditPeriodFragment dialog,
			String savedName) {
		closeKeyboard(dialog);
		SPeriod p = periods.get(chosenIndex);
		// Setting the period's class name
		p.mClassName = savedName;
		mParser.getSData().setPeriodName(p.mPeriodShort, savedName);
		updatePeriods();
	}

	@Override
	public void onDialogNeutralClick(EditPeriodFragment dialog) {
		closeKeyboard(dialog);
		SPeriod p = periods.get(chosenIndex);
		p.mClassName = periods.get(chosenIndex).getDefaultPeriodName();
		mParser.getSData().deletePeriodName(p.mPeriodShort);
		updatePeriods();
	}

	@Override
	public void onDialogNegativeClick(EditPeriodFragment dialog) {
		closeKeyboard(dialog);
	}

	/**
	 * Closes the soft input keyboard if it has been forcefully opened.
	 */
	private void closeKeyboard(EditPeriodFragment d) {
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(d.edit.getApplicationWindowToken(), 0);
		// imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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
				if (mParser.downloadBaseSchedules()) {
					if (mParser.saveMiscDetails())
						return "Y";
				} else {
					return "E";
				}
			return "I";
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("Y")) { // success
				goToSettingPeriod(true);
			} else {
				mParser.getSData().saveInitialize(false);
				if (result.equals("I"))
					// Internet error
					setInitMessage("Please check your connection to the Internet.");
				else
					// general error
					setInitMessage("There was an error. Please try again later or contact the ASB PR.");
			}

			pd.dismiss();
		}

	}

	/**
	 * Downloads the schedule from the online file.
	 * 
	 * @author Sarang
	 */
	private class DownloadScheduleTask extends AsyncTask<Void, Void, String> {
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(SActivity.this, "",
					"Checking for updates...");
		}

		@Override
		protected String doInBackground(Void... params) {
			if (!Network.isConnectedToInternet(SActivity.this)) {
				return null;
			}
			boolean u = mParser.saveAndParseUpdatesFile();
			boolean h = mParser.saveAndParseHolidaysFile();
			if (u && h)
				return "UH";
			else if (u)
				return "U";
			else if (h)
				return "H";
			else
				return "N";
		}

		@Override
		protected void onPostExecute(String result) {
			Toast t;
			if (result != null) {
				if (result.equals("U") || result.equals("UH")) {
					updatePeriods();
					t = Toast.makeText(SActivity.this,
							"Schedule has been updated!", Toast.LENGTH_LONG);
					showAlteredDays();
				} else if (result.equals("H")) {
					updatePeriods();
					t = Toast.makeText(SActivity.this, "New holidays!",
							Toast.LENGTH_LONG);
				} else {
					t = Toast.makeText(SActivity.this, "No updates!",
							Toast.LENGTH_LONG);
				}
				mParser.getSData().saveNotification(false);

				t.show();
			} else {
				t = Toast.makeText(SActivity.this,
						"No connection to the Internet.", Toast.LENGTH_LONG);
			}
			t.show();
			pd.dismiss();
		}
	}

	// SETUP DIFFERENT LAYOUTS
	private void updateLayout() {
		switch (mSState) {
		case DEFAULT:
			setContentView(R.layout.activity_schedule2);
			break;
		case INIT:
			setContentView(R.layout.activity_schedule);
			break;
		case PERIODS:
			setContentView(R.layout.activity_schedule_setperiods);
			break;
		default:
			break;
		}
	}

	/**
	 * Goes to the initialization stage.
	 */
	private void goToInit() {
		mSState = ScheduleState.INIT;
		updateLayout();
		new InitializeScheduleTask(this).execute();
	}

	/**
	 * Goes to the default schedule stage.
	 * 
	 * @param isInit
	 *            whether this is in the process of initialization
	 */
	private void goToDefault(Intent i, boolean isInit) {
		mParser.getSData().saveInitialize(true);
		finish();
		startActivity(i);
	}

	/**
	 * Goes to the period setting stage.
	 * 
	 * @param isInit
	 *            whether this is in the process of initialization
	 */
	private void goToSettingPeriod(boolean isInit) {
		final boolean i = isInit;
		mSState = ScheduleState.PERIODS;
		updateLayout();
		if (isInit)
			Toast.makeText(this,
					"Setup your periods with your current schedule!",
					Toast.LENGTH_LONG).show();
		setPeriodsBtn = (Button) findViewById(R.id.setPeriodsBtn);
		skipPeriodsBtn = (Button) findViewById(R.id.skipBtn);
		clearPeriodsBtn = (Button) findViewById(R.id.clearPeriodsBtn);

		setPeriodsBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				savePeriods(i);
			}
		});
		skipPeriodsBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (i)
					mParser.getSData().saveInitialize(true);
				restartActivity(i);
			}
		});
		clearPeriodsBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearPeriods();
				savePeriods(i);
			}
		});

		setPeriodEditTexts();
	}

	/**
	 * Given whether this is initialization, goes to the default stage of the
	 * activity.
	 * 
	 * @param isInit
	 *            whether this is initialization or not
	 */
	private void restartActivity(boolean isInit) {
		Intent intent = new Intent(this, SActivity.class);
		intent.putExtra(INIT_KEY, isInit);
		goToDefault(intent, isInit);
	}

	// SETTING PERIODS
	// Setup
	private void setPeriodEditTexts() {
		setPeriod((EditText) findViewById(R.id.period1), mParser.getSData()
				.getPeriodName(1 + ""));
		setPeriod((EditText) findViewById(R.id.period2), mParser.getSData()
				.getPeriodName(2 + ""));
		setPeriod((EditText) findViewById(R.id.period3), mParser.getSData()
				.getPeriodName(3 + ""));
		setPeriod((EditText) findViewById(R.id.period4), mParser.getSData()
				.getPeriodName(4 + ""));
		setPeriod((EditText) findViewById(R.id.period5), mParser.getSData()
				.getPeriodName(5 + ""));
		setPeriod((EditText) findViewById(R.id.period6), mParser.getSData()
				.getPeriodName(6 + ""));
		setPeriod((EditText) findViewById(R.id.periodLunch), mParser.getSData()
				.getPeriodName(SStatic.lunch_short));
	}

	private void setPeriod(EditText e, String pn) {
		if (!pn.equals(""))
			e.setText(pn);
		else
			e.setText("");
	}

	private void clearPeriods() {
		if (mSState == ScheduleState.PERIODS) {
			((EditText) findViewById(R.id.period1)).setText("");
			((EditText) findViewById(R.id.period2)).setText("");
			((EditText) findViewById(R.id.period3)).setText("");
			((EditText) findViewById(R.id.period4)).setText("");
			((EditText) findViewById(R.id.period5)).setText("");
			((EditText) findViewById(R.id.period6)).setText("");
			((EditText) findViewById(R.id.periodLunch)).setText("");
		}
	}

	// Saving
	private void savePeriodName(String i, String s) {
		if (!s.equals(""))
			mParser.getSData().setPeriodName(i, s);
		else
			mParser.getSData().deletePeriodName(i);
	}

	private void savePeriods(boolean isInit) {
		savePeriodName(1 + "", ((EditText) findViewById(R.id.period1))
				.getText().toString());
		savePeriodName(2 + "", ((EditText) findViewById(R.id.period2))
				.getText().toString());
		savePeriodName(3 + "", ((EditText) findViewById(R.id.period3))
				.getText().toString());
		savePeriodName(4 + "", ((EditText) findViewById(R.id.period4))
				.getText().toString());
		savePeriodName(5 + "", ((EditText) findViewById(R.id.period5))
				.getText().toString());
		savePeriodName(6 + "", ((EditText) findViewById(R.id.period6))
				.getText().toString());

		savePeriodName(SStatic.lunch_short,
				((EditText) findViewById(R.id.periodLunch)).getText()
						.toString());

		restartActivity(isInit);
	}

	// HELPERS
	private void setInitMessage(String s) {
		if (mSState == ScheduleState.INIT) {
			TextView v = (TextView) findViewById(R.id.messageView);
			v.setText(s);
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

	/**
	 * The actions to be taken when a title button is pressed.
	 * 
	 * @param d the number of days to be "shifted" (roughly)<br>0 - title,<br>1 - next day,<br>-1 - previous day
	 */
	private void titleButtonClicked(int d) {
		SStatic.updateCurrentTime();
		if(d == 0) {
			// Title tapped
			mParser.updateScheduleDay(SStatic.now, true);
		} else {
			// Previous/next day
			mParser.shiftDay(d);
		}
		updatePeriods();
	}
}