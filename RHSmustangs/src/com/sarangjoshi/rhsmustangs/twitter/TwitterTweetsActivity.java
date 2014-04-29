package com.sarangjoshi.rhsmustangs.twitter;

import java.util.ArrayList;

import twitter4j.Status;

import com.sarangjoshi.rhsmustangs.R;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TwitterTweetsActivity extends Activity {
	private ListView tweetsList;
	
	TwitterDataParse tData;
	TwitterNetwork tNetwork;
	
	ArrayList<String> tweets = new ArrayList<String>();
	ArrayList<twitter4j.Status> statuses = new ArrayList<twitter4j.Status>();
	ArrayAdapter<String> tweetsAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitter_tweets);
		
		tData = new TwitterDataParse(this);
		tNetwork = new TwitterNetwork(this);
		
		tweetsList = (ListView) findViewById(R.id.tweetsListView);
		tweetsList.setOnItemClickListener(new MyOnItemClickListener());
		
		loadTweets();
	}
	
	private class MyOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int pos,
				long id) {
			Status s = statuses.get(pos);
			tNetwork.favorite(s.getId());
		}
		
	}
	
	/**
	 * The function to get Tweets from the Data Parser and load them into a
	 * ListView.
	 */
	private void loadTweets() {
		statuses = tData.getTweetStatuses();
		tweets = tData.getTweets(statuses);
		
		tweetsAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, tweets);
		tweetsList.setAdapter(tweetsAdapter);
	}
}
