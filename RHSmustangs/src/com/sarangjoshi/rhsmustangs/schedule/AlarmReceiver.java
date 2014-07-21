/**
 * SBroadcastReceiver.java
 * Jul 19, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.sarangjoshi.rhsmustangs.MyService;

public class AlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		//Toast.makeText(context, "Broadcast received, service started.", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(context, SService.class);
		context.startService(i);
	}
}
