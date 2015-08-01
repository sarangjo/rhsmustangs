package com.sarangjoshi.rhsmustangs.schedule;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.*;
import android.widget.*;

import com.sarangjoshi.rhsmustangs.R;
import com.sarangjoshi.rhsmustangs.content.*;

import java.util.List;

public class UpdatedDaysFragment extends DialogFragment {
    private List<SUpdatedDay> mUpdatedDays;

    public void setData(List<SUpdatedDay> updatedDays) {
        this.mUpdatedDays = updatedDays;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.action_updated_days);

        View v = inflater.inflate(R.layout.dialog_updated_days, container);

        ListView updatedDaysView = (ListView) v.findViewById(R.id.updated_days_list);

        UpdatedDaysAdapter adapter = new UpdatedDaysAdapter(getActivity(),
                android.R.layout.simple_list_item_1);
        adapter.updateData();
        updatedDaysView.setAdapter(adapter);

        return v;
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

            rowView = inflater.inflate(R.layout.layout_updated_day, parent,
                    false);
            content = (TextView) rowView.findViewById(R.id.content);

            content.setText(SStatic.getDisplayString(super.getItem(pos).getDate()));

            return rowView;
        }
    }
}