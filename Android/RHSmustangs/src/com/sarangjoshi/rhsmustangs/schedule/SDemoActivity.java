/**
 * SDemoActivity.java
 * Aug 28, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.sarangjoshi.rhsmustangs.R;

public class SDemoActivity extends FragmentActivity {
	MyPagerAdapter mAdapter;
	ViewPager mPager;
	Button dButton;

	private static int N_OF_ITEMS = 4;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sdemo);

		mAdapter = new MyPagerAdapter(getSupportFragmentManager());

		mPager = (ViewPager) findViewById(R.id.myPager);
		mPager.setAdapter(mAdapter);

		mPager.setCurrentItem(0);

		dButton = (Button) findViewById(R.id.doneButton);
		dButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});
	}

	private class MyPagerAdapter extends FragmentStatePagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			return MyFragment.newInstance(pos);
		}

		@Override
		public int getCount() {
			return N_OF_ITEMS;
		}
	}

	public static class MyFragment extends Fragment {
		int mNum = 0;

		/**
		 * Create a new instance of CountingFragment, providing "num" as an
		 * argument.
		 */
		static MyFragment newInstance(int num) {
			MyFragment f = new MyFragment();

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
			if (getArguments() != null) {
				mNum = getArguments().getInt("num");
				mNum = ((getArguments() != null) ? getArguments().getInt("num")
						: 1);
			}
		}

		/**
		 * The Fragment is created here.
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			switch (mNum) {
			case 0:
				return inflater.inflate(R.layout.pager_item_fragment0,
						container, false);
			case 1:
				return inflater.inflate(R.layout.pager_item_fragment1,
						container, false);
			case 2:
				return inflater.inflate(R.layout.pager_item_fragment2,
						container, false);
			case 3:
				return inflater.inflate(R.layout.pager_item_fragment3,
						container, false);
			}
			return null;
		}
	}
}
