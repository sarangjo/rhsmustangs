package com.sarangjoshi.rhsmustangs.twitter;

public class Tweet {
	private String username;
	private String text;
	
	public Tweet(String newUser, String newText) {
		username = newUser;
		text = newText;
	}
	
	public String getUsername() {
		return username;
	}
	public String getText() {
		return text;
	}
}
