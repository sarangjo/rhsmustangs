package com.sarangjoshi.rhsmustangs.twitter;

import java.util.ArrayList;

import org.json.JSONObject;

import twitter4j.ResponseList;
import twitter4j.Status;

import android.content.Context;

public class TwitterDataParse {
	private ArrayList<String> tweets;
	Context context;
	TwitterNetwork tNetwork;
	
	public TwitterDataParse(Context newContext) {
		context = newContext;
		tweets = new ArrayList<String>();
		tNetwork = new TwitterNetwork(context);
	}

	public ArrayList<String> getTweets() {
		ResponseList<twitter4j.Status> statuses = tNetwork.getTweets();
		String user, text;
		
		for(Status s : statuses) {
			user = s.getUser().getScreenName();
			text = s.getText();
			
			tweets.add(user + ": " + text);
		}
		
		return tweets;
	}
}
