package com.sarangjoshi.rhsmustangs.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;

import com.sarangjoshi.rhsmustangs.MainActivity;
import com.sarangjoshi.rhsmustangs.R;

/**
 * TODO: add class comment
 *
 * @author Sarang Joshi
 */
public class SettingsFragment extends PreferenceFragment {
    public SettingsListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference pref = findPreference(getString(R.string.pref_key_refresh_base));
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mListener.refreshBase();
                return true;
            }
        });
    }

    public static SettingsFragment newInstance(SettingsListener l,
                                               int position) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt(MainActivity.ARG_SECTION_NUMBER, position);
        fragment.setArguments(args);
        fragment.mListener = l;
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(MainActivity.ARG_SECTION_NUMBER));
    }

    public interface SettingsListener {
        public void refreshBase();
    }
}
