/**
 * SSwipeRefreshLayout.java
 * Sep 17, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;

public class SSwipeRefreshLayout extends SwipeRefreshLayout {

	Context ctx;
	ListView lv;

	public SSwipeRefreshLayout(Context context) {
		super(context);
		ctx = context;
	}

	public void setListView(ListView nLv) {
		lv = nLv;
	}

	@Override
	public boolean canChildScrollUp() {
		return lv.getFirstVisiblePosition() != 0;
	}
}
