/**
 * TwitterTweetsActivity.java
 * 30 Apr 2014
 * Sarang Joshi
 */

package com.sarangjoshi.rhsmustangs.twitter;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.sarangjoshi.rhsmustangs.R;

public class TTweetsActivity extends Activity {
	private ListView tweetsList;

	TDataParse tData;
	public static TNetwork tNetwork;

	private ArrayList<String> tweets = new ArrayList<String>();
	private ArrayList<Status> statuses = new ArrayList<Status>();
	//private Status[] statusArray;

	private ArrayList<Boolean> favorites;

	private TweetsAdapter tweetsAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitter_tweets);
	}

	@Override
	public void onStart() {
		super.onStart();
		tData = new TDataParse(this);
		tNetwork = new TNetwork(this);

		tweetsList = (ListView) findViewById(R.id.tweetsListView);

		loadTweets();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.twitter_action_bar, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh_tweets:
			loadTweets();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * The function to get Tweets from the Data Parser and load them into a
	 * ListView.
	 */
	private void loadTweets() {
		ProgressDialog pd = ProgressDialog.show(this, "", "Refreshing...");

		tweets = new ArrayList<String>();
		statuses = new ArrayList<Status>();

		// statuses is a collection of twitter4j "Status" objects.
		statuses = tData.getTweetStatuses();
		// tweets is a collection of parsed String objects.
		tweets = tData.getTweets(statuses);

		pd.dismiss();

		// This array is what the ArrayAdapter uses as its values
		//statusArray = new Status[statuses.size()];
		favorites = new ArrayList<Boolean>();
		for (int i = 0; i < statuses.size(); i++) {
			//statusArray[i] = statuses.get(i);
			// As the statuses are parsed into the array, the parallel boolean
			// ArrayList favorites is also filled up.
			favorites.add(statuses.get(i).isFavorited());
		}
		tweetsAdapter = new TweetsAdapter(this, statuses);
		tweetsList.setAdapter(tweetsAdapter);
		// updateFavorites();
	}

	private class TweetsAdapter extends ArrayAdapter<Status> {
		private final Context mContext;

		// private final Status[] values;

		public TweetsAdapter(Context context, List<Status> newValues) {
			super(context, R.layout.layout_tweet, newValues);

			mContext = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View rowView = inflater.inflate(R.layout.layout_tweet, parent,
					false);
			
			// Individual views
			TextView topView = (TextView) rowView.findViewById(R.id.toptext);
			TextView bottomView = (TextView) rowView
					.findViewById(R.id.bottomtext);
			ImageView favoriteButton = (ImageView) rowView
					.findViewById(R.id.favoriteImage);

			// Setting view data
			Status s = statuses.get(position);

			topView.setText(s.getUser().getScreenName());
			bottomView.setText(s.getText());
			updateIsFav(position, favoriteButton);

			favoriteButton.setOnClickListener(new FavoriteClickListener());

			return rowView;
		}

		/**
		 * Updates the image of the favoriteButton based on whether the given
		 * status is favorited or not.
		 * 
		 * @param mReceiver
		 * @param favoriteButton
		 */
		private void updateIsFav(int i, ImageView favoriteButton) {
			if (favorites.get(i))
				favoriteButton.setImageDrawable(mContext.getResources()
						.getDrawable(R.drawable.star_gold));
			else
				favoriteButton.setImageDrawable(mContext.getResources()
						.getDrawable(R.drawable.star));

		}

		private class FavoriteClickListener implements OnClickListener {
			@Override
			public void onClick(View v) {
				int position = tweetsList.getPositionForView(v);
				Status s = statuses.get(position);
				ImageView imageV = (ImageView) v;

				// if the tweet is not favorited,
				if (!favorites.get(position)) {
					// Set the image to the gold favorited star drawable
					imageV.setImageDrawable(mContext.getResources().getDrawable(
							R.drawable.star_gold));

					// Favorite it on the server
					TTweetsActivity.tNetwork.favorite(s.getId());
					// Favorite it on the local arraylist
					favorites.set(position, true);
				} else {
					// basically the reverse of the above if block.
					// Set the image to the unfavorited star drawable
					imageV.setImageDrawable(mContext.getResources().getDrawable(
							R.drawable.star));

					// Unfavorite it on the server
					TTweetsActivity.tNetwork.unfavorite(s.getId());
					// Favorite it on the local arraylist
					favorites.set(position, false);
				}
			}
		}
	}

}
