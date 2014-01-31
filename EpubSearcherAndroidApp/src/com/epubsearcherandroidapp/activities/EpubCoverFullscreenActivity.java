package com.epubsearcherandroidapp.activities;

import com.epubsearcherandroidapp.R;
import com.epubsearcherandroidapp.adapter.FullscreenCoverAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
 
public class EpubCoverFullscreenActivity extends Activity {
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epub_cover_fullscreen);
 
        // get intent data
        Intent i = getIntent();
 
        // Selected image id
        int position = i.getExtras().getInt("id");
        FullscreenCoverAdapter imageAdapter = new FullscreenCoverAdapter(this);
 
        ImageView imageView = (ImageView) findViewById(R.id.epub_cover_fullscreen);
        imageView.setImageResource(imageAdapter.mThumbIds[position]);
    }
 
}
