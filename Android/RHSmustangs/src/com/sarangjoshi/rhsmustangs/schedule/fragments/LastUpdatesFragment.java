/**
 * LastUpdatesFragment.java
 * Aug 9, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.sarangjoshi.rhsmustangs.schedule.SStatic;

public class LastUpdatesFragment extends DialogFragment {
	public String schedUpdates = "N/A";
	public String holUpdates = "N/A";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String s = "Schedule Updates:\n" + SStatic.getTimeString(schedUpdates)
				+ "\n\nHoliday Updates:\n" + SStatic.getTimeString(holUpdates);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(s).setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// getDialog().dismiss();
					}
				});
		return builder.create();
	}

}
