package com.sarangjoshi.rhsmustangs.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sarangjoshi.rhsmustangs.R;

/**
 * TODO: add class comment
 *
 * @author Sarang Joshi
 */
public class ScheduleDayFragment extends Fragment {
    public static final String ARG = "arg";

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.schedule_day, container, false);

        ((TextView) v.findViewById(R.id.test)).setText("Test " + getArguments().getInt(ARG));

        return v;
    }
}
