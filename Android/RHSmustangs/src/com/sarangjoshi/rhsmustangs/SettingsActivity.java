/**
 * SSettingsActivity.java
 * Sep 17, 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs;

import com.sarangjoshi.rhsmustangs.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment()).commit();
	}

	public static class SettingsFragment extends PreferenceFragment implements
			OnSharedPreferenceChangeListener {
		public static final String IS24HR_KEY = "pref_key_is24hr";

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			addPreferencesFromResource(R.xml.preferences);

			setupPreferences();
		}

		private void setupPreferences() {
			Preference is24hr = findPreference(IS24HR_KEY);
			is24hr.setSummary((PreferenceManager.getDefaultSharedPreferences(
					getActivity()).getBoolean(IS24HR_KEY, true) ? "All times will be shown in 24-hour time."
					: "All times will be shown in 12-hour time."));
		}

		public void onResume() {
			super.onResume();
			getPreferenceScreen().getSharedPreferences()
					.registerOnSharedPreferenceChangeListener(this);
		}

		public void onPause() {
			super.onPause();
			getPreferenceScreen().getSharedPreferences()
					.unregisterOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPrefs,
				String key) {
			if (key.equals(IS24HR_KEY)) {
				Preference is24hr = findPreference(key);
				is24hr.setSummary((sharedPrefs.getBoolean(IS24HR_KEY, true) ? "All times will be shown in 24-hour time."
						: "All times will be shown in 12-hour time."));
			}
		}
	}
}
