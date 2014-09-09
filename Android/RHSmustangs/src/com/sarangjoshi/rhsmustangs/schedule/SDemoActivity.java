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
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.sarangjoshi.rhsmustangs.R;

public class SDemoActivity extends FragmentActivity {
	MyPagerAdapter mAdapter;
	ViewPager mPager;
	Button dButton, sButton;

	private static int N_OF_ITEMS = 5;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sdemo);

		mAdapter = new MyPagerAdapter(getSupportFragmentManager());

		mPager = (ViewPager) findViewById(R.id.myPager);
		mPager.setAdapter(mAdapter);

		OnClickListener t = new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		};
		dButton = (Button) findViewById(R.id.doneDemoButton);
		sButton = (Button) findViewById(R.id.skipDemoButton);
		dButton.setOnClickListener(t);
		sButton.setOnClickListener(t);

		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

			@Override
			public void onPageSelected(int pos) {
				if (pos == N_OF_ITEMS - 1) {
					dButton.setVisibility(View.VISIBLE);
					sButton.setVisibility(View.GONE);
				} else {
					dButton.setVisibility(View.GONE);
					sButton.setVisibility(View.VISIBLE);
				}
			}

		});
		mPager.setPageTransformer(true, new MyTransformer());
	}

	private class MyPagerAdapter extends FragmentStatePagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			return SDemoFragment.newInstance(pos);
		}

		@Override
		public int getCount() {
			return N_OF_ITEMS;
		}
	}

	private class MyTransformer implements ViewPager.PageTransformer {

		@Override
		public void transformPage(View v, float position) {
			if (position < -1) {
				v.setAlpha(0);
			} else if (position <= 1) {
				v.setAlpha(1 - Math.abs(position));
			} else {
				v.setAlpha(0);
			}
		}

	}

}
