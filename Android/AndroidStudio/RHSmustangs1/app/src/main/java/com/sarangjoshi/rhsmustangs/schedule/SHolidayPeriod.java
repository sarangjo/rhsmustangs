/**
 * SHolidayPeriod.java
 * Sep 29, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.schedule;

import android.text.format.Time;

public class SHolidayPeriod {
	private Time start;
	private Time end;
	private SDay[] holidays;
	private String name;

	public SHolidayPeriod(String data) {
		
	}

	public SDay getDay(Time t) {
		return null;
	}

	private Time getStart() {
		return new Time();
	}

	private Time getEnd() {
		return new Time();
	}
}
