package com.sarangjoshi.rhsmustangs.twitter;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;

public class TwitterDataParse {
	private ArrayList<Tweet> tweets;
	Context context;
	TwitterNetwork tNetwork;
	
	public TwitterDataParse(Context newContext) {
		context = newContext;
	}

	public ArrayList<Tweet> parseData() {
		
		
		return tweets;
	}
}
