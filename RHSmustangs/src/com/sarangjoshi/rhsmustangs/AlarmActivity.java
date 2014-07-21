/**
 * AlarmActivity.java
 * Jul 18, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AlarmActivity extends Activity {
	AlarmManager alarmManager;
	PendingIntent operation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_alarm);
	}

	public void stopAlarm(View v) {
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(operation);
	}

	public void startAlarm(View v) {
		Calendar cal = Calendar.getInstance();

		Intent broadcastIntent = new Intent(this, MyBroadcastReceiver.class);
		operation = PendingIntent.getBroadcast(this, 0, broadcastIntent, 0);

		// Intent serviceIntent = new Intent(AlarmActivity.this,
		// MyService.class);
		// operation = PendingIntent.getService(this, 0, serviceIntent, 0);

		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				cal.getTimeInMillis(), 10 * 1000, operation);
		// alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
		// + (10 * 1000), operation);

		Toast.makeText(this, "Alarm started; 10 seconds", Toast.LENGTH_LONG)
				.show();
	}
}
