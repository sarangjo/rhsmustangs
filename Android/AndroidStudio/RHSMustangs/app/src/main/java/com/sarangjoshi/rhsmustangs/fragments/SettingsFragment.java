package com.sarangjoshi.rhsmustangs.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;

import com.sarangjoshi.rhsmustangs.MainActivity;
import com.sarangjoshi.rhsmustangs.R;

/**
 * TODO: add class comment
 *
 * @author Sarang Joshi
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    public static SettingsFragment newInstance(int position) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt(MainActivity.ARG_SECTION_NUMBER, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(MainActivity.ARG_SECTION_NUMBER));
    }
}
