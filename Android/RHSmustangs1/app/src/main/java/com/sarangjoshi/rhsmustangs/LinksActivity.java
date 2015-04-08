package com.sarangjoshi.rhsmustangs;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LinksActivity extends Activity {
	// Link[] links = new Link[4];
	HashMap<String, String> linkmap = new HashMap<String, String>();
	String[] linkNames;

	ListView listView;
	GridView gridView;
	ArrayAdapter<String> linksAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_links);

		setLinks();
		setupListView();
		// setupGridView();
	}

	private void setupGridView() {
		gridView = (GridView) findViewById(R.id.linksGrid);
		gridView.setAdapter(new ImageAdapter(this));

		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Toast.makeText(LinksActivity.this, "" + position,
						Toast.LENGTH_SHORT).show();
			}
		});

	}

	private class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private Integer[] mThumbIds = { R.drawable.ic_lwsd,
				R.drawable.ic_facebook, R.drawable.ic_twitter,
				R.drawable.ic_instagram };

		public ImageAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {
			return mThumbIds.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			} else {
				imageView = (ImageView) convertView;
			}

			imageView.setImageResource(mThumbIds[position]);
			return imageView;
		}

	}

	private void setupListView() {
		linksAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, linkNames);

		listView = (ListView) this.findViewById(R.id.linksListView);
		listView.setAdapter(linksAdapter);

		OnItemClickListener mItemListener = new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position,
					long id) {
				if (position != 1)
					openWebURL(linkmap.get(((TextView) v).getText()));
				else
					startActivity(getFacebookIntent());
			}
		};

		listView.setOnItemClickListener(mItemListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_action_bar, menu);
		return true;
	}

	/**
	 * Sets all the links to be shown.
	 * 
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

	public Intent getFacebookIntent() {
		try {
			getPackageManager().getPackageInfo("com.facebook.katana", 0);
			return new Intent(Intent.ACTION_VIEW,
					Uri.parse("fb://page/320359521377855"));
		} catch (Exception e) {
			return new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://www.facebook.com/redmondhighasb"));
		}
	}

	public void openWebURL(String inURL) {
		Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(inURL));
		startActivity(browse);
	}
}
