/**
 * ConfirmResetFragment.java
 * Aug 9, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.sarangjoshi.rhsmustangs.schedule.SActivity;

public class ConfirmResetFragment extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Confirm reset everything?")
				.setPositiveButton(android.R.string.yes, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						((SActivity) getActivity()).resetEverything();
					}
				})
				.setNegativeButton(android.R.string.no, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
		return builder.create();
	}
}
