package com.sarangjoshi.rhsmustangs.schedule;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.*;
import android.widget.*;

import com.sarangjoshi.rhsmustangs.R;
import com.sarangjoshi.rhsmustangs.content.*;
import com.sarangjoshi.rhsmustangs.helper.SHelper;

import java.util.List;

public class UpdatedDaysFragment extends DialogFragment {
    private List<SUpdatedDay> mUpdatedDays;
    private UpdatedDaySelectedListener mListener;

    public UpdatedDaysFragment(List<SUpdatedDay> updatedDays, UpdatedDaySelectedListener l) {
        this.mUpdatedDays = updatedDays;
        this.mListener = l;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.action_updated_days);

        View v = inflater.inflate(R.layout.dialog_updated_days, container);

        ListView updatedDaysView = (ListView) v.findViewById(R.id.updated_days_list);

        updatedDaysView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                mListener.updatedDaySelected(position);
            }
        });

        UpdatedDaysAdapter adapter = new UpdatedDaysAdapter(getActivity(),
                android.R.layout.simple_list_item_1);
        adapter.updateData();
        updatedDaysView.setAdapter(adapter);

        return v;
    }

    public interface UpdatedDaySelectedListener {
        /**
         * Called when the updated day has been selected.
         *
         * @param index the index of the selected updated day
         */
        void updatedDaySelected(int index);
    }

    private class UpdatedDaysAdapter extends ArrayAdapter<SUpdatedDay> {
        private final Context mContext;

        public UpdatedDaysAdapter(Context context, int resource) {
            super(context, resource);

            mContext = context;
        }

        public void updateData() {
            super.clear();
            super.addAll(mUpdatedDays);
        }

        public View getView(int pos, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView;
            TextView content;

            rowView = inflater.inflate(android.R.layout.simple_list_item_1, parent,
                    false);
            content = (TextView) rowView.findViewById(android.R.id.text1);

            content.setText(SHelper.getDisplayString(super.getItem(pos).getDate()));

            return rowView;
        }
    }
}