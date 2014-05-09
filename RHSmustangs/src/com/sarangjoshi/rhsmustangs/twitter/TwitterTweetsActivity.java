package com.sarangjoshi.rhsmustangs.twitter;

import java.util.ArrayList;

import twitter4j.Status;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.sarangjoshi.rhsmustangs.R;

public class TwitterTweetsActivity extends Activity {
	private ListView tweetsList;

	TwitterDataParse tData;
	public static TwitterNetwork tNetwork;

	ArrayList<String> tweets = new ArrayList<String>();
	ArrayList<Status> statuses = new ArrayList<Status>();
	// Status[] statusArray = new Status[1];
	// ArrayAdapter<String> tweetsAdapter;
	TweetsAdapter tweetsAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitter_tweets);
	}

	@Override
	public void onStart() {
		super.onStart();
		tData = new TwitterDataParse(this);
		tNetwork = new TwitterNetwork(this);

		tweetsList = (ListView) findViewById(R.id.tweetsListView);
		// tweetsList.setOnItemClickListener(new TweetListItemClickListener());

		loadTweets();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.my_action_bar, menu);

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
		
		statuses = tData.getTweetStatuses();
		tweets = tData.getTweets(statuses);

		pd.dismiss();
		/*
		 * tweetsAdapter = new ArrayAdapter<String>(this,
		 * android.R.layout.simple_list_item_1, tweets);
		 * tweetsList.setAdapter(tweetsAdapter);
		 */
		Status[] statusArray = new Status[statuses.size()];
		for (int i = 0; i < statuses.size(); i++) {
			statusArray[i] = statuses.get(i);
		}
		tweetsAdapter = new TweetsAdapter(this, statusArray);
		tweetsList.setAdapter(tweetsAdapter);
		// updateFavorites();
	}
}
