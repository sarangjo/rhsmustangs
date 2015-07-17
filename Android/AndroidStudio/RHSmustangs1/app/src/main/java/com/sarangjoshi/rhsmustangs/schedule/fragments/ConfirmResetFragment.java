/**
 * ConfirmResetFragment.java
 * Aug 9, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.sarangjoshi.rhsmustangs.schedule.SActivity;

public class ConfirmResetFragment extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(
				"This will reset the default base schedule.\nCaution: This will also reset your schedule!")
				.setPositiveButton(android.R.string.yes, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dismiss();
						Intent i = new Intent();
						SActivity x = (SActivity) getActivity();
						x.onActivityResult(SActivity.RESET_BASE_REQUEST,
								Activity.RESULT_OK, i);
					}
				})
				.setNegativeButton(android.R.string.no, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// do nothing
					}

				});
		return builder.create();
	}
}
