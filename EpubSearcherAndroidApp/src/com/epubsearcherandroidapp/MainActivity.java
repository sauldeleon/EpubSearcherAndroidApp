package com.epubsearcherandroidapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

public class MainActivity extends Activity {
	
	final static private String APP_KEY = "4lux9y10nib629k";
	final static private String APP_SECRET = "bub07yv0jzjqg4d";
	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;

	private DropboxAPI<AndroidAuthSession> mDBApi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// In the class declaration section:

		// And later in some initialization function:
		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true; 
	}

}
