package com.sarangjoshi.rhsmustangs.twitter;

import twitter4j.Status;

import com.sarangjoshi.rhsmustangs.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TweetsAdapter extends ArrayAdapter<Status> {
	private final Context context;
	private final Status[] values;
	
	public TweetsAdapter(Context context, Status[] values) {
		super(context, R.layout.layout_tweet, values);
		
		this.context = context;
		this.values = values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View rowView = inflater.inflate(R.layout.layout_tweet, parent, false);
		TextView topView = (TextView) rowView.findViewById(R.id.toptext);
		TextView bottomView = (TextView) rowView.findViewById(R.id.bottomtext);
		
		Status s = values[position];
		topView.setText(s.getUser().getScreenName());
		bottomView.setText(s.getText());
		
		return rowView;
	}

}
