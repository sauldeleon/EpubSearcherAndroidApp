package com.epubsearcherandroidapp;

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

public class ListingActivity extends Activity{
	
	private GridView listingGridView;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		String[] list = (String[]) b.getSerializable("listado");
		
		// Basic Android widgets
		setContentView(R.layout.activity_listing);
		
		listingGridView = (GridView)findViewById(R.id.listingGridView);
		ArrayAdapter<String> ad = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		listingGridView.setBackgroundColor(Color.BLACK);
		listingGridView.setNumColumns(1);
		listingGridView.setGravity(Gravity.CENTER);
		listingGridView.setAdapter(ad);			
		OnItemClickListener l = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Toast.makeText(getApplicationContext(),
						((TextView) v).getText(), Toast.LENGTH_SHORT).show();
				return;
			}
		};
		listingGridView.setOnItemClickListener(l);
	}
}
