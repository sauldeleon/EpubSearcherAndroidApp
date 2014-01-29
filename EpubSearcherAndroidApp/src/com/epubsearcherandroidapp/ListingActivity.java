package com.epubsearcherandroidapp;

import java.util.HashMap;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

public class ListingActivity extends Activity {

	private GridView listingGridView;

	public static DropboxAPI<AndroidAuthSession> mDBApi = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		HashMap<String, EntryMetadata> list = (HashMap<String, EntryMetadata>) b
				.getSerializable("listado");
		String[] list2 = null;
		list2 = list.keySet().toArray(new String[0]);

		// Basic Android widgets
		setContentView(R.layout.activity_listing);

		listingGridView = (GridView) findViewById(R.id.listingGridView);
		ArrayAdapter<String> ad = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list2);
		listingGridView.setBackgroundColor(Color.WHITE);
		listingGridView.setNumColumns(1);
		listingGridView.setGravity(Gravity.CENTER);
		listingGridView.setAdapter(ad);
		listingGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				String path = ((TextView) v).getText().toString();
				FileListing fileListing = new FileListing(ListingActivity.this,
						mDBApi, path);
				fileListing.execute();
				Toast.makeText(getApplicationContext(),
						((TextView) v).getText(), Toast.LENGTH_SHORT).show();
				return;
			}
		});
	}
}
