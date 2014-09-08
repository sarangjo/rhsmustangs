/**
 * SDemoFragment.java
 * Sep 8, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sarangjoshi.rhsmustangs.R;

public class SDemoFragment extends Fragment {
	int mNum = 0;

	/**
	 * Create a new instance of CountingFragment, providing "num" as an
	 * argument.
	 */
	public static SDemoFragment newInstance(int num) {
		SDemoFragment f = new SDemoFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);

		return f;
	}

	/**
	 * When creating, retrieve this instance's number from its arguments.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
			mNum = getArguments().getInt("num");
	}

	/**
	 * The Fragment is created here.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.pager_item_fragment, container,
				false);
		ImageView x = (ImageView) v.findViewById(R.id.pagerItemImage);

		switch (mNum) {
		case 0:
			x.setImageResource(R.drawable.demo0);
			break;
		case 1:
			x.setImageResource(R.drawable.demo1);
			break;
		case 2:
			x.setImageResource(R.drawable.demo2);
			break;
		case 3:
			x.setImageResource(R.drawable.demo3);
			break;
		case 4:
			x.setImageResource(R.drawable.demo4);
			break;
		}
		return x;

	}
}
