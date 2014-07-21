/**
 * SService.java
 * Jul 19, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import com.sarangjoshi.rhsmustangs.R;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

public class SService extends IntentService {
	private int result = Activity.RESULT_CANCELED;

	SNetwork mNet;
	SData mData;

	public static final String UPDATES_AVAILABLE_KEY = "ua";
	public static final String RESULT_KEY = "result";
	public static final String NOTIFICATION_ACTION = "com.sarangjoshi.rhsmustangs.schedule";

	public SService() {
		super("SService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
		boolean isUpdated = checkForUpdates();
		if (isUpdated)
			createNotification();
	}

	/**
	 * Returns whether the online file has updates.
	 */
	private boolean checkForUpdates() {
		mNet = new SNetwork();
		mData = new SData(this);
		String netUpdate = mNet.getLatestUpdate();
		if (netUpdate.equals(mData.getUpdateTime()))
			return false;
		return true;
	}

	/**
	 * Notifies the user that the schedule has been updated.
	 */
	private void createNotification() {
		NotificationCompat.Builder b = new NotificationCompat.Builder(this);

		b.setSmallIcon(R.drawable.rhslogo_green);
		b.setContentTitle("Schedule updates available.");
		b.setContentText("The schedule has updates! Click to find out more.");
		b.setAutoCancel(true);

		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.setBigContentTitle("Schedule updates available.");
		bigTextStyle.bigText("The schedule has updates! Click to find out more.");

		b.setStyle(bigTextStyle);

		Intent resultIntent = new Intent(this, SActivity.class);

		resultIntent.putExtra(UPDATES_AVAILABLE_KEY, true);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(SActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);

		b.setContentIntent(resultPendingIntent);
		NotificationManager mNotifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		mNotifManager.notify(0, b.build());
		Toast.makeText(this, "Notification created.", Toast.LENGTH_SHORT)
				.show();
	}
}
