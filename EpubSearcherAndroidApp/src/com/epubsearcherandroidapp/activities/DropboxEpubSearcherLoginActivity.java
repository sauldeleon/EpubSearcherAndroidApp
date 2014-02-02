package com.epubsearcherandroidapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;
import com.epubsearcherandroidapp.R;
import com.epubsearcherandroidapp.tasks.FileListing;

/**
 * @author Saúl de León
 * 
 */
public class DropboxEpubSearcherLoginActivity extends Activity {

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
	private Button reListButton;
	private CheckBox checkboxRecursiveMode;
	private CheckBox checkBoxAutoTitleMode;
	private boolean logged;
	private boolean firstLogin;

	// private Button listingButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidAuthSession session = buildSession();
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
		firstLogin = false;

		// Configurar la interfaz
		setContentView(R.layout.activity_main);
		configureRecursiveOptionCheckBox();
		configureTitleOptionCheckBox();
		configureEnterButton();
		configureReListButton();
	}

	/**
	 * Configures a new checkBox for the showTitle option
	 */
	private void configureTitleOptionCheckBox() {
		checkBoxAutoTitleMode = (CheckBox) findViewById(R.id.checkBoxEpubTitle);

		checkBoxAutoTitleMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkBoxAutoTitleMode.isChecked()) {
					createAlertDialog("En función del número de epubs que tenga, marcar esta opción puede ser más costosa");
				}
			}
		});
	}

	/**
	 * Configures a new checkBox for the recursive option
	 */
	private void configureRecursiveOptionCheckBox() {
		checkboxRecursiveMode = (CheckBox) findViewById(R.id.checkBoxRecursive);

		checkboxRecursiveMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkboxRecursiveMode.isChecked()) {
					createAlertDialog("Marcando esta opción, puede tardar varios minutos en listar todos tus libros electrónicos");
				}
			}
		});
	}

	/**
	 * Configures a dialog in for the checkboxes activation
	 */
	private void createAlertDialog(String text) {
		new AlertDialog.Builder(DropboxEpubSearcherLoginActivity.this).setTitle("Atención").setMessage(text).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// dejarlo como esta
			}
		}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				checkBoxAutoTitleMode.toggle();
			}
		}).setIcon(R.drawable.icon_alert).show();
	}

	/**
	 * Configures the login Button
	 */
	private void configureEnterButton() {
		loginButton = (Button) findViewById(R.id.enterButton);

		loginButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (logged) {
					logOut();
				} else {
					mDBApi.getSession().startAuthentication(DropboxEpubSearcherLoginActivity.this);
				}
			}
		});
	}
	
	private void configureReListButton() {
		reListButton = (Button) findViewById(R.id.reListButton);

		reListButton.setVisibility(View.INVISIBLE);
		reListButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (logged) {
					boolean isRecursiveSearch = checkboxRecursiveMode.isChecked();
					boolean isAutoTitleSearch = checkBoxAutoTitleMode.isChecked();
					FileListing fileListing = new FileListing(DropboxEpubSearcherLoginActivity.this, mDBApi, "/", isRecursiveSearch, isAutoTitleSearch);
					fileListing.execute();
				} 
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		AndroidAuthSession session = mDBApi.getSession();

		// para recuperar la sesión del ultimo login y no tener que relogear
		if (session.authenticationSuccessful()) {
			try {
				session.finishAuthentication();

				// guardamos localmente la sesión
				TokenPair tokens = session.getAccessTokenPair();
				storeKeys(tokens.key, tokens.secret);
				setLoggedIn(true);

				// default order by name
				if (!firstLogin) {
					firstLogin = true;
					boolean isRecursiveSearch = checkboxRecursiveMode.isChecked();
					boolean isAutoTitleSearch = checkBoxAutoTitleMode.isChecked();
					FileListing fileListing = new FileListing(DropboxEpubSearcherLoginActivity.this, mDBApi, "/", isRecursiveSearch, isAutoTitleSearch);
					fileListing.execute();
				}
			} catch (IllegalStateException e) {
				showToast("No se pudo autenticar con Dropbox" + e.getLocalizedMessage());
				Log.i(TAG, "Error de login", e);
			}
		}
	}

	/**
	 * creates a session with dropbpox
	 * 
	 * @return the session in dropbox api
	 */
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

	/**
	 * log out from dropbpox
	 */
	private void logOut() {
		// Borramos credenciales de la sesion
		mDBApi.getSession().unlink();
		// Borramos las claves del ultimo acceso
		clearKeys();
		// Cambiamos la UI al modo de unlinked
		setLoggedIn(false);
		firstLogin = false;
	}

	/**
	 * this method retrieves the keys of the last login session active in
	 * dropbox
	 * 
	 * @return keys of the last login
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

	/**
	 * stores the key-secret pair for the dropbox login
	 * 
	 * @param key
	 * @param secret
	 */
	private void storeKeys(String key, String secret) {
		// Save the access key for later
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.putString(ACCESS_KEY_NAME, key);
		edit.putString(ACCESS_SECRET_NAME, secret);
		edit.commit();
	}

	/**
	 * removes the keys of the dropbox session
	 */
	private void clearKeys() {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}

	/**
	 * Changes in the UI when login state changes
	 */
	private void setLoggedIn(boolean loggedIn) {
		logged = loggedIn;
		if (loggedIn) {
			reListButton.setVisibility(View.VISIBLE);
			loginButton.setText("Desconectar");
		} else {
			reListButton.setVisibility(View.INVISIBLE);
			loginButton.setText("Entrar a Dropbox");
		}
	}

	/**
	 * retrieve errors by toast
	 */
	private void showToast(String msg) {
		Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		error.show();
	}
}
