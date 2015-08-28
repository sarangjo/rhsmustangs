package com.sarangjoshi.rhsmustangs.schedule;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sarangjoshi.rhsmustangs.R;
import com.sarangjoshi.rhsmustangs.content.SHoliday;
import com.sarangjoshi.rhsmustangs.helper.SHelper;

import java.util.List;

/**
 * TODO: add class comment
 *
 * @author Sarang Joshi
 */

public class HolidaysFragment extends DialogFragment {
    private List<SHoliday> mHolidays;
    private HolidaySelectedListener mListener;

    public HolidaysFragment(List<SHoliday> holidays, HolidaySelectedListener l) {
        this.mHolidays = holidays;
        this.mListener = l;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.action_holidays);

        View v = inflater.inflate(R.layout.dialog_holidays, container);

        ListView holidaysView = (ListView) v.findViewById(R.id.holidays_list);

        if(mHolidays.isEmpty()) {
            TextView noDaysView = (TextView) v.findViewById(R.id.no_holidays);

            noDaysView.setVisibility(View.VISIBLE);
            holidaysView.setVisibility(View.GONE);

            return v;
        }

        holidaysView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                mListener.holidaySelected(position);
            }
        });

        HolidaysAdapter adapter = new HolidaysAdapter(getActivity(),
                android.R.layout.simple_list_item_1);
        adapter.updateData();
        holidaysView.setAdapter(adapter);

        return v;
    }

    public interface HolidaySelectedListener {
        /**
         * Called when the updated day has been selected.
         *
         * @param index the index of the selected updated day
         */
        void holidaySelected(int index);
    }

    private class HolidaysAdapter extends ArrayAdapter<SHoliday> {
        private final Context mContext;

        public HolidaysAdapter(Context context, int resource) {
            super(context, resource);

            mContext = context;
        }

        public void updateData() {
            super.clear();
            super.addAll(mHolidays);
        }

        public View getView(int pos, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView;
            TextView content;

            rowView = inflater.inflate(android.R.layout.simple_list_item_1, parent,
                    false);
            content = (TextView) rowView.findViewById(android.R.id.text1);

            content.setText(super.getItem(pos).toString());

            return rowView;
        }
    }
}