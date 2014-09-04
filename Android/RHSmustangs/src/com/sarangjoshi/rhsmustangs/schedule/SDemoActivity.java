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
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sarangjoshi.rhsmustangs.R;

public class SDemoActivity extends FragmentActivity {
	MyPagerAdapter mAdapter;
	ViewPager mPager;

	private static int N_OF_ITEMS = 5;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sdemo);

		mAdapter = new MyPagerAdapter(getSupportFragmentManager());

		mPager = (ViewPager) findViewById(R.id.myPager);
		mPager.setAdapter(mAdapter);

		mPager.setCurrentItem(0);
	}

	private class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			return MyFragment.newInstance(pos, getIntent().getIntExtra("y", 0));
		}

		@Override
		public int getCount() {
			return N_OF_ITEMS;
		}
	}

	public static class MyFragment extends Fragment {
		int mNum = 0;
		int mY = 0;

		/**
		 * Create a new instance of CountingFragment, providing "num" as an
		 * argument.
		 */
		static MyFragment newInstance(int num, int newY) {
			MyFragment f = new MyFragment();

			// Supply num input as an argument.
			Bundle args = new Bundle();
			args.putInt("num", num);
			args.putInt("y", newY);
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
			View v = inflater.inflate(R.layout.pager_item_fragment,
					container, false);
			
			TextView tv = (TextView) v.findViewById(R.id.pagerTView);
			tv.setText(getDemoText(mNum));
			tv.setY(mY);

			return v;
		}

		private String getDemoText(int n) {
			switch (n) {
			case 0:
				return "These are the periods. Hold a period to customize it.";
			case 1:
				return "Tap the title to return to today's most relevant schedule. Tap the gift to go to the next upcoming holiday. Move forward and back using arrows...";
			case 2:
				return "... or by swiping left and right.";
			case 3:
				return "Tap here to check for schedule updates. Choose your period group here.";
			case 4:
				return "To see all updated schedule days, and more, tap here. All done!";
			default:
				return "LOL WE JUST GOT #HACKED";
			}
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
		}
	}
}
