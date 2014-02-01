package com.epubsearcherandroidapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.epubsearcherandroidapp.R;
 
public class EpubCoverFullscreenActivity extends Activity {
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epub_cover_fullscreen);
 
        // get intent data
        Intent i = getIntent();
 
        // Selected image id
        Bitmap bitmap = (Bitmap) i.getParcelableExtra("portada");
       
        ImageView imageView = (ImageView) findViewById(R.id.epub_cover_fullscreen);
        imageView.setImageBitmap(bitmap);
    }
 
}
