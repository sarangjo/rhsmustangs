/**
 * Network.java
 * May 12, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Network {

	/**
	 * Checks for connection.
	 * 
	 * @return if it is connected to the Internet
	 */
	public static boolean isConnectedToInternet(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager != null) {
			NetworkInfo[] info = manager.getAllNetworkInfo();

			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
