package com.sarangjoshi.rhsmustangs.twitter;

import java.util.ArrayList;

import com.sarangjoshi.rhsmustangs.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TwitterTweetsActivity extends Activity {
	private ListView tweetsList;
	
	TwitterDataParse tData;
	ArrayList<String> tweets = new ArrayList<String>();
	ArrayAdapter<String> tweetsAdapter;


	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_tweets);
		
		tData = new TwitterDataParse(this);
		tweetsList = (ListView) findViewById(R.id.tweetsListView);
		
		loadTweets();
	}
	
	/**
	 * The function to get Tweets from the Data Parser and load them into a
	 * ListView.
	 */
	private void loadTweets() {
		tweets = tData.getTweets();

		tweetsAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, tweets);
		tweetsList.setAdapter(tweetsAdapter);
	}


}
