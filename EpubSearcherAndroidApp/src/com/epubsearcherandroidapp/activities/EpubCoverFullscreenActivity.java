package com.epubsearcherandroidapp.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.epubsearcherandroidapp.R;
 
/**
 * @author Saúl de León
 * This class creates an activity to show an epub'x cover in fullscreen
 *
 */
public class EpubCoverFullscreenActivity extends Activity {
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epub_cover_fullscreen);
  
        // take the bitmap from the intent
        Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("epubCover");
       
        ImageView imageView = (ImageView) findViewById(R.id.epub_cover_fullscreen);
        imageView.setImageBitmap(bitmap);
    } 
}
