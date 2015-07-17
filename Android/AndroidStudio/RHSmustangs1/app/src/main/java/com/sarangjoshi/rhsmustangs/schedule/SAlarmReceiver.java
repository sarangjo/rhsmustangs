/**
 * AlarmReceiver.java
 * Jul 19, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SAlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// Toast.makeText(context, "Broadcast received, service started.",
		// Toast.LENGTH_SHORT).show();
		Intent i = new Intent(context, SService.class);
		if (shouldStart())
			context.startService(i);
	}

	private boolean shouldStart() {
		Calendar cal = Calendar.getInstance();
		int hr = cal.get(Calendar.HOUR_OF_DAY);
		int day = cal.get(Calendar.DAY_OF_WEEK);
		
		// Checks whether the alarm is in range
		if (hr >= 16 && hr <= 22)
			return (day >= Calendar.SUNDAY && day <= Calendar.THURSDAY);
		if (hr >= 6 && hr <= 8)
			return (day >= Calendar.MONDAY && day <= Calendar.FRIDAY);

		return false;
	}
}
