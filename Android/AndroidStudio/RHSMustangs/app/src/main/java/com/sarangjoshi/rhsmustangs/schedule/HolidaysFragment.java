package com.sarangjoshi.rhsmustangs.schedule;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sarangjoshi.rhsmustangs.R;
import com.sarangjoshi.rhsmustangs.content.SHoliday;
import com.sarangjoshi.rhsmustangs.content.SUpdatedDay;
import com.sarangjoshi.rhsmustangs.helper.SHelper;

import java.util.List;

/**
 * TODO: add class comment
 *
 * @author Sarang Joshi
 */
public class HolidaysFragment extends DialogFragment {
    private List<SHoliday> mHolidays;
    private HolidaySelectedListener l;

    public HolidaysFragment(List<SHoliday> days, HolidaySelectedListener l) {
        this.mHolidays = days;
        this.l = l;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        ListAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
                mHolidays);
        builder.setTitle("Updated Days")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        l.holidaySelected(which);
                    }
                });

        return builder.create();
    }

    public interface HolidaySelectedListener {
        /**
         * Called when the updated day has been selected.
         *
         * @param index the index of the selected updated day
         */
        void holidaySelected(int index);
    }
}
