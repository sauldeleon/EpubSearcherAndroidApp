package com.epubsearcherandroidapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.epubsearcherandroidapp.R;

public class ImageAdapterWithText extends BaseAdapter {

	private Context context;
	private final String[] values;
	

	public ImageAdapterWithText(Context context, String[] textValues) {
		this.context = context;
		this.values = textValues;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View gridView;
		
		gridView = new View(context);
		gridView = inflater.inflate(R.layout.grid_file, null);

		String[] splitted = values[position].split(" -.- ");		
		// set value into textview
		TextView textView = (TextView) gridView.findViewById(R.id.gridTextViewTitle);
		textView.setText(splitted[0]);

		// set value into textview
		TextView dateView = (TextView) gridView.findViewById(R.id.gridTextViewDate);
		dateView.setText(splitted[1]);

		// set image based on selected text
		ImageView imageView = (ImageView) gridView.findViewById(R.id.gridView_icon);

		if (values[position].contains("-F-")) {
			imageView.setImageResource(R.drawable.folder_icon);
		} else if (values[position].contains("-#-")) {
			imageView.setImageResource(R.drawable.epub_icon);
		}
		return gridView;
	}

	@Override
	public int getCount() {
		return values.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}