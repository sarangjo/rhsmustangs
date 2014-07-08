/**
 * SelectAltDayFragment.java
 * Jul 8, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

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

public class SelectAltDayFragment extends DialogFragment {

	ListView vAlteredDays;

	String[] mAlteredDaysText;

	public void setAlteredDays(String[] alteredDaysText) {
		mAlteredDaysText = new String[alteredDaysText.length + 1];
		mAlteredDaysText[0] = "(Return)";
		for (int i = 0; i < alteredDaysText.length; i++) {
			mAlteredDaysText[i + 1] = SStaticData.getDateString(alteredDaysText[i]);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle("Altered Days");

		View v = inflater.inflate(R.layout.layout_alt_day_select, container);

		vAlteredDays = (ListView) v.findViewById(R.id.altered_days_list);
		vAlteredDays.setOnItemClickListener(new OnItemClickListener() {

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
				mAlteredDaysText);
		vAlteredDays.setAdapter(daysAdapter);

		return v;
	}

}
