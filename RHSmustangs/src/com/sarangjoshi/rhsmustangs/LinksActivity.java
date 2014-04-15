package com.sarangjoshi.rhsmustangs;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Set;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LinksActivity extends Activity {
	Link[] links = new Link[4];
	String[] linkNames;
	
	ListView listView;
	ArrayAdapter<String> linksAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_links);

		setLinks();
		
		linksAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, linkNames);
		
		listView = (ListView) this.findViewById(R.id.linksListView);
		listView.setAdapter(linksAdapter);
		
		OnItemClickListener mItemListener = new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				openWebURL(links[position].url);
			}
		};
		
		listView.setOnItemClickListener(mItemListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * Sets all the links to be shown.
	 * @throws MalformedURLException lol
	 */
	public void setLinks() {
		links[0] = new Link("LWSD Page",
				"http://www.lwsd.org/school/rhs/Pages/default.aspx");
		links[1] = new Link("Facebook", "http://www.facebook.com/redmondhighasb");
		links[2] = new Link("Twitter", "http://www.twitter.com/redmondasb");
		links[3] = new Link("Instagram", "http://www.instagram.com/redmondasb");

		linkNames = new String[links.length];
		for(int i = 0; i < links.length; i++) {
			linkNames[i] = links[i].name;
		}
	}
	
	public void openWebURL(String inURL) {
		Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(inURL));
		startActivity(browse);
	}
}
