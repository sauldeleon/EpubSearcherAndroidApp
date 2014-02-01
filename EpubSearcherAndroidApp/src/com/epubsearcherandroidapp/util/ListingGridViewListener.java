package com.epubsearcherandroidapp.util;

import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.epubsearcherandroidapp.activities.ListingActivity;
import com.epubsearcherandroidapp.tasks.FileDownloader;
import com.epubsearcherandroidapp.tasks.FileListing;

/**
 * @author Saúl de León
 * This class is a try to implements double tap in an epub file. in development
 *
 */
public class ListingGridViewListener extends SimpleOnGestureListener implements OnTouchListener {
	ListingActivity context;
	GestureDetector gDetector;
	
	private View v;

	public ListingGridViewListener(ListingActivity context) {
		this(context, null);
	}

	public ListingGridViewListener(ListingActivity context, GestureDetector gDetector) {

		if (gDetector == null)
			gDetector = new GestureDetector(context, this);

		this.context = context;
		this.gDetector = gDetector;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		
		TextView name = ((TextView) ((RelativeLayout) v).getChildAt(2));
		TextView date = ((TextView) ((RelativeLayout) v).getChildAt(1));

		String text = name.getText().toString() + " -F- " + date.getText().toString();
		String path = context.getNameShownPathMap().get(text);
		if (path == null) {
			text = name.getText().toString() + " -#- " + date.getText().toString();
			path = context.getNameShownPathMap().get(text);
		}
		if (context.getListFiles().get(path).getIsDir()) {
			FileListing fileListing = new FileListing(context, context.getmDBApi(), path, false, context.getTitleMode());
			fileListing.execute();
		} 
		Toast.makeText(context, name.getText(), Toast.LENGTH_SHORT).show();
		return true;
	}
	
	@Override
	public boolean onDoubleTap(MotionEvent e) {
		
		TextView name = ((TextView) ((RelativeLayout) v).getChildAt(2));
		TextView date = ((TextView) ((RelativeLayout) v).getChildAt(1));

		String text = name.getText().toString() + " -F- " + date.getText().toString();
		String path = context.getNameShownPathMap().get(text);
		if (path == null) {
			text = name.getText().toString() + " -#- " + date.getText().toString();
			path = context.getNameShownPathMap().get(text);
		}
		if (!context.getListFiles().get(path).getIsDir()) {
			FileDownloader epubDownloader = new FileDownloader(context, context.getmDBApi(), path);
			epubDownloader.execute();
		} 
		Toast.makeText(context, name.getText(), Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		this.v=v;
		return gDetector.onTouchEvent(event);
	}

	public GestureDetector getDetector() {
		return gDetector;
	}
}