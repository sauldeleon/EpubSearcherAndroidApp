package com.epubsearcherandroidapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;

public class DropboxEpubSearcher extends Activity {

	private static final String TAG = "Dropbox";

	// Dropbox
	final static private String APP_KEY = "4lux9y10nib629k";
	final static private String APP_SECRET = "bub07yv0jzjqg4d";
	final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;

	// Atributos para el uso de la API de Dropbox para android
	final static private String ACCOUNT_PREFS_NAME = "prefs";
	final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
	final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";

	private DropboxAPI<AndroidAuthSession> mDBApi;

	private Button loginButton;
	private boolean logged;

	// private Button listingButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidAuthSession session = buildSession();
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);

		// Basic Android widgets
		setContentView(R.layout.activity_main);

		loginButton = (Button) findViewById(R.id.buttonLoggin);

		loginButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (logged) {
					logOut();
				} else {
					mDBApi.getSession().startAuthentication(DropboxEpubSearcher.this);
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		AndroidAuthSession session = mDBApi.getSession();

		// para recuperar la sesión del último login y no tener que relogear
		if (session.authenticationSuccessful()) {
			try {
				session.finishAuthentication();

				// guardamos localmente la sesión
				TokenPair tokens = session.getAccessTokenPair();
				storeKeys(tokens.key, tokens.secret);
				setLoggedIn(true);
				// default order by path
				FileListing fileListing = new FileListing(DropboxEpubSearcher.this, mDBApi, "/");
				fileListing.execute();
			} catch (IllegalStateException e) {
				showToast("No se pudo autenticar con Dropbox" + e.getLocalizedMessage());
				Log.i(TAG, "Error de login", e);
			}
		}
	}

	private AndroidAuthSession buildSession() {
		AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session;

		// construimos una sesion a partir de los datos almacenados previamente,
		// si existen
		String[] stored = getKeys();
		if (stored != null) {
			AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
		} else {
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
		}

		return session;
	}

	private void logOut() {
		// Borramos credenciales de la sesion
		mDBApi.getSession().unlink();
		// Borramos las claves del ultimo acceso
		clearKeys();
		// Cambiamos la UI al modo de unlinked
		setLoggedIn(false);
	}

	/**
	 * @return las claves para el ultimo login
	 */
	private String[] getKeys() {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		String key = prefs.getString(ACCESS_KEY_NAME, null);
		String secret = prefs.getString(ACCESS_SECRET_NAME, null);
		if (key != null && secret != null) {
			String[] ret = new String[2];
			ret[0] = key;
			ret[1] = secret;
			return ret;
		} else {
			return null;
		}
	}

	private void storeKeys(String key, String secret) {
		// Save the access key for later
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.putString(ACCESS_KEY_NAME, key);
		edit.putString(ACCESS_SECRET_NAME, secret);
		edit.commit();
	}

	private void clearKeys() {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}

	/**
	 * EN esta función se hace el cambio de UI cuando hay un cambio en el login
	 */
	private void setLoggedIn(boolean loggedIn) {
		logged = loggedIn;
		if (loggedIn) {
			loginButton.setText("Desconectar");
		} else {
			loginButton.setText("Entrar a Dropbox");
		}
	}

	/**
	 * metodo para sacar errores
	 */
	private void showToast(String msg) {
		Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		error.show();
	}
}
