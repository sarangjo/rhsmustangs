/**
 * MyBroadcastReceiver.java
 * Jul 18, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "Broadcast received", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(context, MyService.class);
		context.startService(i);
	}

}
