/**
 * SService.java
 * Jul 19, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import java.util.Calendar;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.sarangjoshi.rhsmustangs.Network;
import com.sarangjoshi.rhsmustangs.R;

public class SService extends IntentService {
	SNetwork mNet;
	SData mData;

	String net = "", dat = "";

	public static final String UPDATES_AVAILABLE_KEY = "ua";
	public static final String RESULT_KEY = "result";
	public static final String NOTIFICATION_ACTION = "com.sarangjoshi.rhsmustangs.schedule";

	public static final int NOTIF_ID = 0;

	public SService() {
		super("SService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		setup();
		Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
		if (Network.isConnectedToInternet(this)) {
			boolean isUpdated = checkForUpdates();
			if (isUpdated)
				if (!mData.getIsNotifCreated())
					createNotification();
		}
	}

	/**
	 * Sets up SNetwork and SData objects.
	 */
	private void setup() {
		if (mNet == null)
			mNet = new SNetwork();
		if (mData == null)
			mData = new SData(this);
	}

	/**
	 * Returns whether the online file has updates.
	 */
	private boolean checkForUpdates() {
		net = mNet.getLatestUpdateTime();
		dat = mData.getUpdateTime();

		if (net.equals(dat) || net.equals("NA") || dat.equals("")
				|| net.trim().length() != SStaticData.DATE_LENGTH)
			return false;
		return true;
	}

	/**
	 * Notifies the user that the schedule has been updated.
	 */
	private void createNotification() {
		NotificationCompat.Builder b = new NotificationCompat.Builder(this);

		String title = "Schedule updates available.";
		String text = net + "\n" + dat;

		Calendar cal = Calendar.getInstance();
		text += "\nUpdated: " + cal.get(Calendar.HOUR_OF_DAY) + ":"
				+ cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);

		// Setup small notification
		b.setSmallIcon(R.drawable.rhslogo_green);
		b.setContentTitle(title);
		b.setContentText(text);
		b.setAutoCancel(true);

		// Setup big notification
		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.setBigContentTitle(title);
		bigTextStyle.bigText(text);
		b.setStyle(bigTextStyle);

		// Setup ContentIntent
		Intent resultIntent = new Intent(this, SActivity.class);
		resultIntent.putExtra(UPDATES_AVAILABLE_KEY, true);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(SActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);

		b.setContentIntent(resultPendingIntent);
		NotificationManager mNotifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		mNotifManager.notify(NOTIF_ID, b.build());

		mData.saveNotification(true);

		Toast.makeText(this, "Notification created.", Toast.LENGTH_SHORT)
				.show();
	}
}
