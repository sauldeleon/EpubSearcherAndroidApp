package com.epubsearcherandroidapp.adapter;

import com.epubsearcherandroidapp.activities.EpubCoverFullscreenActivity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class FullscreenCoverAdapter extends BaseAdapter {

	private EpubCoverFullscreenActivity mContext;
	
	public Integer[] mThumbIds = {};

	public FullscreenCoverAdapter(EpubCoverFullscreenActivity epubCoverFullscreenActivity) {
		mContext = epubCoverFullscreenActivity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
