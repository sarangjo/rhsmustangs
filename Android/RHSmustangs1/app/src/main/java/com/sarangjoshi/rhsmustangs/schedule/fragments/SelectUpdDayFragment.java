/**
 * SelectAltDayFragment.java
 * Jul 8, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sarangjoshi.rhsmustangs.R;
import com.sarangjoshi.rhsmustangs.schedule.SActivity;
import com.sarangjoshi.rhsmustangs.schedule.SStatic;

public class SelectUpdDayFragment extends DialogFragment {

	ListView vUpdatedDays;
	String[] mUpdatedDaysText;

	public void setUpdatedDays(String[] updatedDaysText) {
		if (updatedDaysText != null) {
			mUpdatedDaysText = new String[updatedDaysText.length + 1];
			mUpdatedDaysText[0] = "(Return)";
			for (int i = 0; i < updatedDaysText.length; i++) {
				mUpdatedDaysText[i + 1] = SStatic
						.getDateString(updatedDaysText[i]);
			}
		} else
			mUpdatedDaysText = new String[] { "(Return)" };
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle("Updated Days");

		View v = inflater.inflate(R.layout.layout_alt_day_select, container);

		vUpdatedDays = (ListView) v.findViewById(R.id.altered_days_list);
		vUpdatedDays.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				getDialog().dismiss();
				// 0 = Return
				if (position > 0) {
					Intent i = new Intent();
					i.putExtra(SActivity.SCHEDULE_INDEX_KEY, position);
					SActivity x = (SActivity) getActivity();
					x.onActivityResult(SActivity.PICK_SCHEDULE_REQUEST,
							Activity.RESULT_OK, i);
				}
			}

		});

		ArrayAdapter<String> daysAdapter = new ArrayAdapter<String>(
				this.getActivity(), android.R.layout.simple_list_item_1,
				mUpdatedDaysText);
		vUpdatedDays.setAdapter(daysAdapter);

		return v;
	}

}
