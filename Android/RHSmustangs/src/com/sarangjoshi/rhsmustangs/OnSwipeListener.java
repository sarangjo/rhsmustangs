/**
 * OnSwipeListener.java
 * Jul 22, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class OnSwipeListener implements OnTouchListener {

	private final GestureDetector gestureDetector;

	public OnSwipeListener(Context context) {
		gestureDetector = new GestureDetector(context, new GestureListener());
	}

	private final class GestureListener extends SimpleOnGestureListener {
		private static final int SWIPE_THRESHOLD = 50;
		private static final int SWIPE_VEL_THRESHOLD = 50;

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velX,
				float velY) {
			try {
				// e1 is the start, e2 is the finish.
				float dX = e2.getX() - e1.getX();
				float dY = e2.getY() - e1.getY();

				if (Math.abs(dX) > Math.abs(dY)) {
					// Case 1 - swipes in the X direction
					// Check thresholds
					if (Math.abs(dX) > SWIPE_THRESHOLD
							&& Math.abs(velX) > SWIPE_VEL_THRESHOLD) {
						if (dX > 0)
							onSwipeRight();
						else
							onSwipeLeft();
					}
				} else {
					// Case 2 - swipes in the Y direction
					// Check thresholds
					if (Math.abs(dY) > SWIPE_THRESHOLD
							&& Math.abs(velY) > SWIPE_VEL_THRESHOLD) {
						if (dY > 0)
							onSwipeDown();
						else
							onSwipeUp();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	public void onSwipeRight() {
	}

	public void onSwipeLeft() {
	}

	public void onSwipeUp() {
	}

	public void onSwipeDown() {
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

}
