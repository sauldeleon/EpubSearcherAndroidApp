package com.epubsearcherandroidapp.activities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.epubsearcherandroidapp.R;
import com.epubsearcherandroidapp.adapter.ImageAdapterWithText;
import com.epubsearcherandroidapp.tasks.FileDownloader;
import com.epubsearcherandroidapp.tasks.FileListing;
import com.epubsearcherandroidapp.util.EntryMetadata;

public class ListingActivity extends Activity {

	private GridView listingGridView;
	// private ArrayAdapter<String> adGridView;

	private Spinner orderSpinner;

	public static DropboxAPI<AndroidAuthSession> mDBApi = null;

	private HashMap<String, EntryMetadata> listFiles;

	private HashMap<String, String> nameShownPathMap = new HashMap<String, String>();

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		listFiles = (HashMap<String, EntryMetadata>) b.getSerializable("listado");

		// Basic Android widgets
		setContentView(R.layout.activity_listing);

		configureGridView();

		configureSpinner();

	}

	private void configureSpinner() {
		orderSpinner = (Spinner) findViewById(R.id.orderSpinner);
		orderSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				String[] shownList;
				ArrayList<EntryMetadata> orderedValues;
				if (position == 1) {
					// ordenar por nombre de archivo
					orderedValues = order(position);
					shownList = generateViewNames(orderedValues);
					ImageAdapterWithText n = new ImageAdapterWithText(ListingActivity.this, shownList);
					n.notifyDataSetInvalidated();
					listingGridView.setAdapter(n);
				} else if (position == 2) {
					// ordenar por fecha de modificacion
					orderedValues = order(position);
					shownList = generateViewNames(orderedValues);
					ImageAdapterWithText n = new ImageAdapterWithText(ListingActivity.this, shownList);
					n.notifyDataSetInvalidated();
					listingGridView.setAdapter(n);
				}
				return;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void configureGridView() {
		ArrayList<EntryMetadata> orderedValues = order(1);
		String[] shownList = generateViewNames(orderedValues);

		listingGridView = (GridView) findViewById(R.id.listingGridView);
		
		listingGridView.setBackgroundColor(Color.WHITE);
		listingGridView.setNumColumns(1);
		listingGridView.setGravity(Gravity.CENTER);
		listingGridView.setAdapter(new ImageAdapterWithText(this, shownList));
		listingGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				TextView name = ((TextView) ((RelativeLayout) v).getChildAt(2));
				TextView date = ((TextView) ((RelativeLayout) v).getChildAt(1));
				
				String text = name.getText().toString() + " -F- " + date.getText().toString();
				String path = nameShownPathMap.get(text);
				if (path == null) {
					text = name.getText().toString() + " -#- " + date.getText().toString();
					path = nameShownPathMap.get(text);
				}
				if (listFiles.get(path).getIsDir()) {
					FileListing fileListing = new FileListing(ListingActivity.this, mDBApi, path, false);
					fileListing.execute();
				} else {
					FileDownloader epubDownloader = new FileDownloader(ListingActivity.this,mDBApi,path);
					epubDownloader.execute();
				}
				Toast.makeText(getApplicationContext(), name.getText(), Toast.LENGTH_SHORT).show();
				return;
			}

		});
	}

	private String[] generateViewNames(ArrayList<EntryMetadata> orderedValues) {
		String[] resp = null;
		ArrayList<String> aux = new ArrayList<String>();
		nameShownPathMap = new HashMap<String, String>();
		for (Iterator<EntryMetadata> iterator = orderedValues.iterator(); iterator.hasNext();) {
			EntryMetadata e = iterator.next();
			String name = e.getName();
			String date = e.getModificationDate();
			if (e.getIsDir()) {
				aux.add(name + " -F- " + date);
				this.nameShownPathMap.put(name + " -F- " + date, e.getPath());
			} else {
				aux.add(name + " -#- " + date);
				this.nameShownPathMap.put(name + " -#- " + date, e.getPath());
			}
		}
		resp = aux.toArray(new String[0]);
		return resp;
	}

	private ArrayList<EntryMetadata> order(int orderBy) {
		ArrayList<EntryMetadata> values = new ArrayList<EntryMetadata>(listFiles.values());

		if (orderBy == 0) {
			// order by path
			Comparator<EntryMetadata> compareByPath = new Comparator<EntryMetadata>() {
				// comparador por path
				@Override
				public int compare(EntryMetadata arg0, EntryMetadata arg1) {
					String a0 = arg0.getPath().toUpperCase();
					String a1 = arg1.getPath().toUpperCase();
					return a0.compareTo(a1);
				}
			};
			Collections.sort(values, compareByPath);
		} else if (orderBy == 1) {
			// order by name
			Comparator<EntryMetadata> compareByName = new Comparator<EntryMetadata>() {
				// comparador por nombre
				@Override
				public int compare(EntryMetadata arg0, EntryMetadata arg1) {
					String a0 = arg0.getName().toUpperCase();
					String a1 = arg1.getName().toUpperCase();
					return a0.compareTo(a1);
				}
			};
			Collections.sort(values, compareByName);
		} else if (orderBy == 2) {
			// order by modification date
			Comparator<EntryMetadata> compareByDate = new Comparator<EntryMetadata>() {
				// comparador por fecha
				@Override
				public int compare(EntryMetadata arg0, EntryMetadata arg1) {

					DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					Date d0 = null, d1 = null;
					try {
						d0 = formatter.parse(arg0.getModificationDate());
						d1 = formatter.parse(arg1.getModificationDate());
					} catch (ParseException e) {
						e.printStackTrace();
					}
					return d0.compareTo(d1);
				}
			};
			Collections.sort(values, compareByDate);
		}
		return values;
	}
}
