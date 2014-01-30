package com.epubsearcherandroidapp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

public class ListingActivity extends Activity {

	private GridView listingGridView;
	private ArrayAdapter<String> adGridView;

	private Spinner orderSpinner;

	public static DropboxAPI<AndroidAuthSession> mDBApi = null;

	private HashMap<String, EntryMetadata> listFiles;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		listFiles = (HashMap<String, EntryMetadata>) b
				.getSerializable("listado");

		// la primera vez mostramos los paths
		String[] pathList = listFiles.keySet().toArray(new String[0]);

		// Basic Android widgets
		setContentView(R.layout.activity_listing);

		listingGridView = (GridView) findViewById(R.id.listingGridView);
		adGridView = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, pathList);
		listingGridView.setBackgroundColor(Color.WHITE);
		listingGridView.setNumColumns(1);
		listingGridView.setGravity(Gravity.CENTER);
		listingGridView.setAdapter(adGridView);
		listingGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// ojo con esto, aqui SIEMPRE tiene que ir el path

				String text = ((TextView) v).getText().toString();

				// de donde saco la ruta?
				String path = searchPath(text);
				FileListing fileListing = new FileListing(ListingActivity.this,
						mDBApi, path);
				fileListing.execute();
				Toast.makeText(getApplicationContext(),
						((TextView) v).getText(), Toast.LENGTH_SHORT).show();
				return;
			}

		});

		orderSpinner = (Spinner) findViewById(R.id.orderSpinner);
		orderSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				String[] shownList;
				if (position == 1) {
					// ordenar por nombre de archivo
					shownList = order(position);
					adGridView = new ArrayAdapter<String>(ListingActivity.this,
							android.R.layout.simple_list_item_1, shownList);
					adGridView.notifyDataSetChanged();
					listingGridView.setAdapter(adGridView);
				} else if (position == 2) {
					// ordenar por fecha de modificacion
					shownList = order(position);
					adGridView = new ArrayAdapter<String>(ListingActivity.this,
							android.R.layout.simple_list_item_1, shownList);
					adGridView.notifyDataSetChanged();
					listingGridView.setAdapter(adGridView);
				}
				return;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private String searchPath(String text) {
		String path = "";
		if (text.startsWith("/")) {
			path = text;
		} else {
			for (Iterator<EntryMetadata> iterator = listFiles.values()
					.iterator(); iterator.hasNext();) {
				EntryMetadata e = iterator.next();
				String fileName = text.substring(0, text.indexOf("-#-"));
				if (e.getName().contains(fileName.trim())) {
					path = e.getPath();
					break;
				}
			}
		}
		return path;
	}

	private String[] order(int orderBy) {
		String[] resp = null;
		ArrayList<String> aux = new ArrayList<String>();

		Collection<EntryMetadata> v = listFiles.values();
		ArrayList<EntryMetadata> values = new ArrayList<EntryMetadata>(v);

		if (orderBy == 0) {
			// order by path
			Comparator<EntryMetadata> compareByPath = new Comparator<EntryMetadata>() {
				// comparador por path
				@Override
				public int compare(EntryMetadata arg0, EntryMetadata arg1) {
					String a0 = arg0.getPath();
					String a1 = arg1.getPath();
					return a0.compareTo(a1);
				}
			};
			Collections.sort(values, compareByPath);
			for (Iterator<EntryMetadata> iterator = values.iterator(); iterator
					.hasNext();) {
				String path = iterator.next().getPath();
				aux.add(path);
			}
		} else if (orderBy == 1) {
			// order by name
			Comparator<EntryMetadata> compareByName = new Comparator<EntryMetadata>() {
				// comparador por nombre
				@Override
				public int compare(EntryMetadata arg0, EntryMetadata arg1) {
					String a0 = arg0.getName();
					String a1 = arg1.getName();
					return a0.compareTo(a1);
				}
			};
			Collections.sort(values, compareByName);
			for (Iterator<EntryMetadata> iterator = values.iterator(); iterator
					.hasNext();) {
				EntryMetadata e = iterator.next();
				String name = e.getName();
				String date = e.getModificationDate();
				aux.add(name + " -#- " + date);
			}
		} else if (orderBy == 2) {
			// order by modification date
			Comparator<EntryMetadata> compareByDate = new Comparator<EntryMetadata>() {
				// comparador por fecha
				@Override
				public int compare(EntryMetadata arg0, EntryMetadata arg1) {
					String a0 = arg0.getModificationDate();
					String a1 = arg1.getModificationDate();
					return a0.compareTo(a1);
				}
			};
			Collections.sort(values, compareByDate);
			for (Iterator<EntryMetadata> iterator = values.iterator(); iterator
					.hasNext();) {
				EntryMetadata e = iterator.next();
				String name = e.getName();
				String date = e.getModificationDate();
				aux.add(name + " -#- " + date);
			}
		}
		resp = aux.toArray(new String[0]);
		return resp;
	}
}
