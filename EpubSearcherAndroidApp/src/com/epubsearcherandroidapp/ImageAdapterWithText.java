package com.epubsearcherandroidapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapterWithText extends BaseAdapter {
    
	private Context context;
	private final String[] textValues;
 
	public ImageAdapterWithText(Context context, String[] textValues) {
		this.context = context;
		this.textValues = textValues;
	}
 
	public View getView(int position, View convertView, ViewGroup parent) {
 
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View gridView;
 
		if (convertView == null) {
			gridView = new View(context);
			gridView = inflater.inflate(R.layout.grid_file, null);
 
            // set value into textview
			TextView textView = (TextView) gridView.findViewById(R.id.gridTextView);
			textView.setText(textValues[position]);
 
			//set image based on selected text
			ImageView imageView = (ImageView) gridView
					.findViewById(R.id.gridView_icon);
 
			String text = textValues[position];
 
			if (text.contains("-F-")) {
				imageView.setImageResource(R.drawable.folder_icon);
			} else if (text.contains("-#-")) {
				imageView.setImageResource(R.drawable.epub_icon);
			}
		} else {
			gridView = (View) convertView;
		}
		return gridView;
	}
 
    @Override
    public int getCount() {
        return textValues.length;
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