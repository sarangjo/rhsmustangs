/**
 * SService.java
 * Jul 14, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs;

import com.sarangjoshi.rhsmustangs.R;
import com.sarangjoshi.rhsmustangs.schedule.SActivity;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

public class MyService extends IntentService {
	
	public MyService() {
		super("MyService");
	}

	@Override
	public void onHandleIntent(Intent intent) {
		Toast.makeText(this, "Service started.", Toast.LENGTH_SHORT).show();
		createNotification(true);
	}

	/**
	 * Creates a notification.
	 * 
	 * @param x a boolean
	 */
	private void createNotification(boolean x) {
		NotificationCompat.Builder b = new NotificationCompat.Builder(this);

		b.setSmallIcon(R.drawable.rhslogo_green);
		b.setContentTitle("Notification");
		b.setContentText("Sup sup sup, World! " + x);
		b.setAutoCancel(true);

		Intent resultIntent = new Intent(this, SActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(SActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);

		b.setContentIntent(resultPendingIntent);
		NotificationManager mNotifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		mNotifManager.notify(0, b.build());
		Toast.makeText(this, "Notification created.", Toast.LENGTH_LONG).show();
	}
}
