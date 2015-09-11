package com.sarangjoshi.rhsmustangs.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sarangjoshi.rhsmustangs.MainActivity;
import com.sarangjoshi.rhsmustangs.R;

/**
 * TODO: add class comment
 *
 * @author Sarang Joshi
 */
public class HomeFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        TextView text = (TextView) v.findViewById(R.id.welcome_text);
        text.setText("Welcome to " + getString(R.string.app_name) + "!");

        return v;
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(MainActivity.ARG_SECTION_NUMBER));
    }
}
