package com.epubsearcherandroidapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;

public class ListingActivity extends Activity {

	private GridView listingGridView;
	private ArrayAdapter<String> adGridView;

	private Spinner orderSpinner;

	public static DropboxAPI<AndroidAuthSession> mDBApi = null;

	private HashMap<String, EntryMetadata> listFiles;

	private HashMap<String, String> nameShownPathMap = new HashMap<String, String>();

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		listFiles = (HashMap<String, EntryMetadata>) b
				.getSerializable("listado");

		// Basic Android widgets
		setContentView(R.layout.activity_listing);

		configureGridView();

		configureSpinner();

	}

	private void configureSpinner() {
		orderSpinner = (Spinner) findViewById(R.id.orderSpinner);
		orderSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				String[] shownList;
				ArrayList<EntryMetadata> orderedValues;
				if (position == 1) {
					// ordenar por nombre de archivo
					orderedValues = order(position);
					shownList = generateViewNames(orderedValues);
					// adGridView = new
					// ArrayAdapter<String>(ListingActivity.this,
					// android.R.layout.simple_list_item_1, shownList);
					// adGridView.notifyDataSetChanged();
					// listingGridView.setAdapter(adGridView);
					ImageAdapterWithText n = new ImageAdapterWithText(
							ListingActivity.this, shownList);
					n.notifyDataSetChanged();
					listingGridView.setAdapter(n);
				} else if (position == 2) {
					// ordenar por fecha de modificacion
					orderedValues = order(position);
					shownList = generateViewNames(orderedValues);
					// adGridView = new
					// ArrayAdapter<String>(ListingActivity.this,
					// android.R.layout.simple_list_item_1, shownList);
					// adGridView.notifyDataSetChanged();
					// listingGridView.setAdapter(adGridView);
					ImageAdapterWithText n = new ImageAdapterWithText(
							ListingActivity.this, shownList);
					n.notifyDataSetChanged();
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
		// adGridView = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, shownList);

		listingGridView.setBackgroundColor(Color.WHITE);
		listingGridView.setNumColumns(1);
		listingGridView.setGravity(Gravity.CENTER);
		// listingGridView.setAdapter(adGridView);
		listingGridView.setAdapter(new ImageAdapterWithText(this, shownList));
		listingGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				String text = ((TextView) v).getText().toString();
				String path = nameShownPathMap.get(text);
				if (!path.endsWith(".epub")) {
					FileListing fileListing = new FileListing(
							ListingActivity.this, mDBApi, path);
					fileListing.execute();
				} else {
					try {
						String baseDir = Environment
								.getExternalStorageDirectory()
								.getAbsolutePath();
						String filename = "prueba.epub";
						File file = new File(baseDir + File.separator
								+ filename);
						FileOutputStream outputStream = new FileOutputStream(
								file);

						DropboxAPI<AndroidAuthSession> mDBApi2 = mDBApi;
						DropboxFileInfo info = mDBApi2.getFile(path, null,
								outputStream, null);
						Log.i("DbExampleLog",
								"The file's rev is: " + info.getMetadata().rev);

						InputStream is = getAssets().open(path);
						Book book = new EpubReader().readEpub(is);
						Metadata metadata = book.getMetadata();
						String bookInfo = "：" + metadata.getAuthors() + "\n ："
								+ metadata.getPublishers() + "\n ："
								+ metadata.getDates() + "\n ："
								+ metadata.getTitles() + "\n ："
								+ metadata.getDescriptions() + "\n ："
								+ metadata.getLanguage() + "\n\n ：";
						Log.e("epublib", bookInfo);
						logTableOfContents(book.getTableOfContents()
								.getTocReferences(), 0);

					} catch (IOException e) {
						Log.e("epublib", e.getMessage());
					} catch (DropboxException e) {

						e.printStackTrace();
					}
				}
				Toast.makeText(getApplicationContext(),
						((TextView) v).getText(), Toast.LENGTH_SHORT).show();
				return;
			}

		});
	}

	private void logTableOfContents(List<TOCReference> tocReferences, int depth) {
		if (tocReferences == null) {
			return;
		}
		for (TOCReference tocReference : tocReferences) {
			StringBuilder tocstring = new StringBuilder();
			for (int i = 0; i < depth; i++) {
				tocstring.append("\t");
			}
			HashMap<String, String> map = new HashMap<String, String>();
			String k = tocstring.append(tocReference.getTitle()).toString();
			ArrayList<HashMap<String, String>> list1 = new ArrayList<HashMap<String, String>>();
			list1.add(map);
			String t = k;
			Log.i("epublib", tocstring.toString());
			logTableOfContents(tocReference.getChildren(), depth + 1);

		}
	}

	private String[] generateViewNames(ArrayList<EntryMetadata> orderedValues) {
		String[] resp = null;
		ArrayList<String> aux = new ArrayList<String>();
		nameShownPathMap = new HashMap<String, String>();
		for (Iterator<EntryMetadata> iterator = orderedValues.iterator(); iterator
				.hasNext();) {
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
		ArrayList<EntryMetadata> values = new ArrayList<EntryMetadata>(
				listFiles.values());

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
