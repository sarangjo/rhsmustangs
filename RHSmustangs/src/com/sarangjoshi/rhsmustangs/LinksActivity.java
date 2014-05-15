package com.sarangjoshi.rhsmustangs;

import java.net.MalformedURLException;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LinksActivity extends Activity {
	// Link[] links = new Link[4];
	HashMap<String, String> linkmap = new HashMap<String, String>();
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
			public void onItemClick(AdapterView parent, View v, int position,
					long id) {
				openWebURL(linkmap.get(((TextView) v).getText()));
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
	 * 
	 * @throws MalformedURLException
	 *             lol
	 */
	public void setLinks() {
		linkmap.put("LWSD Page",
				"http://www.lwsd.org/school/rhs/Pages/default.aspx");
		linkmap.put("Facebook", "http://www.facebook.com/redmondhighasb");
		linkmap.put("Twitter", "http://www.twitter.com/redmondasb");
		linkmap.put("Instagram", "http://www.instagram.com/redmondasb");

		linkNames = new String[linkmap.size()];
		linkNames[0] = "LWSD Page";
		linkNames[1] = "Facebook";
		linkNames[2] = "Twitter";
		linkNames[3] = "Instagram";
	}

	public void openWebURL(String inURL) {
		Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(inURL));
		startActivity(browse);
	}
}
