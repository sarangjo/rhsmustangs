package com.sarangjoshi.rhsmustangs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sarangjoshi.rhsmustangs.schedule.SActivity;

public class MainActivity extends Activity {
	ListView listView;

	String[] viewNames = { "Links", "Schedule" };
	ArrayAdapter<String> adapter;

	public final static String EXTRA_MESSAGE = "com.sarangjoshi.rhsmustangs.MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// ArrayAdapter to fill data
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, viewNames);

		// Initializes List based on adapter.
		listView = (ListView) this.findViewById(R.id.mainListView);
		listView.setAdapter(adapter);

		// Creates item click listener
		OnItemClickListener listClickHandler = new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				listClicked(position);
			}
		};

		listView.setOnItemClickListener(listClickHandler);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_action_bar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Method to run when the main list view has been clicked.
	 * 
	 * @param mainListPosition
	 *            the position of the list that has been clicked
	 */
	public void listClicked(int mainListPosition) {
		Intent intent = null;
		switch (mainListPosition) {
		case 0:
			intent = new Intent(this, LinksActivity.class);
			break;
		case 1:
			intent = new Intent(this, SActivity.class);
			break;
		}
		if (intent != null)
			startActivity(intent);
	}
}
