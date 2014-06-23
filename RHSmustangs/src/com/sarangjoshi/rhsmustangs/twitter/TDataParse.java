package com.sarangjoshi.rhsmustangs.twitter;

import java.util.ArrayList;

import org.json.JSONObject;

import twitter4j.ResponseList;
import twitter4j.Status;

import android.content.Context;

public class TDataParse {
	//private ArrayList<String> tweets;
	Context context;
	TNetwork tNetwork;
	
	public TDataParse(Context newContext) {
		context = newContext;
		tNetwork = new TNetwork(context);
	}

	public ArrayList<twitter4j.Status> getTweetStatuses() {
		ResponseList<twitter4j.Status> statuses = tNetwork.getTweets();
		ArrayList<twitter4j.Status> statusList = new ArrayList<twitter4j.Status>();
		
		for(Status s : statuses) {
			statusList.add(s);
		}
		
		return statusList;
	}
	
	public ArrayList<String> getTweets(ArrayList<twitter4j.Status> statuses) {
		String user, text;
		ArrayList<String> tweets = new ArrayList<String>();
		
		for(Status s : statuses) {
			user = s.getUser().getScreenName();
			text = s.getText();
			
			tweets.add(user + ": " + text);
		}
		
		return tweets;
	}
}
