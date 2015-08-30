package com.sarangjoshi.rhsmustangs.schedule;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.sarangjoshi.rhsmustangs.content.SUpdatedDay;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: add class comment
 *
 * @author Sarang Joshi
 */
public class UpdatedDaysFragment extends DialogFragment {
    private List<SUpdatedDay> mUpdatedDays;
    private UpdatedDaySelectedListener l;

    public UpdatedDaysFragment(List<SUpdatedDay> days, UpdatedDaySelectedListener l) {
        this.mUpdatedDays = days;
        this.l = l;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        ListAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item,
                mUpdatedDays);
        builder.setTitle("Updated Days")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        l.updatedDaySelected(which);
                    }
                });

        return builder.create();
    }

    public interface UpdatedDaySelectedListener {
        /**
         * Called when the updated day has been selected.
         *
         * @param index the index of the selected updated day
         */
        void updatedDaySelected(int index);
    }
}
