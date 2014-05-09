package com.sarangjoshi.rhsmustangs.twitter;

import java.util.ArrayList;

import twitter4j.Status;

import com.sarangjoshi.rhsmustangs.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetsAdapter extends ArrayAdapter<Status> {
	private final Context context;
	private final Status[] values;
	private int currentPos;

	public TweetsAdapter(Context context, Status[] values) {
		super(context, R.layout.layout_tweet, values);

		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.layout_tweet, parent, false);
		TextView topView = (TextView) rowView.findViewById(R.id.toptext);
		TextView bottomView = (TextView) rowView.findViewById(R.id.bottomtext);
		ImageView favoriteButton = (ImageView) rowView
				.findViewById(R.id.favoriteImage);

		// View data
		Status s = values[position];
		String text = s.getText();
		topView.setText(s.getUser().getScreenName());
		bottomView.setText(text);
		updateIsFav(s, favoriteButton);
		currentPos = position;

		favoriteButton.setOnClickListener(new FavoriteClickListener());

		return rowView;
	}
	
	private void updateIsFav(Status s, ImageView favoriteButton) {
		if (s.isFavorited())
			favoriteButton.setImageDrawable(context.getResources().getDrawable(
					R.drawable.star_gold));
		else
			favoriteButton.setImageDrawable(context.getResources().getDrawable(
					R.drawable.star));
		
	}

	private class FavoriteClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			ImageView imageV = (ImageView) v;
			imageV.setImageDrawable(context.getResources().getDrawable(
					R.drawable.star_gold));

			Status s = values[currentPos];
			TwitterTweetsActivity.tNetwork.favorite(s.getId());
		}
	}
}
