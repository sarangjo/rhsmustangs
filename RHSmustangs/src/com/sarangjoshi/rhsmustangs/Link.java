package com.sarangjoshi.rhsmustangs;

import java.net.MalformedURLException;
import java.net.URL;

public class Link {
	public String name;
	public String url;
	
	public Link(String newURL) throws MalformedURLException {
		url = newURL;
	}
	public Link(String newName, String newURL) {
		url = newURL;
		name = newName;
	}
}
