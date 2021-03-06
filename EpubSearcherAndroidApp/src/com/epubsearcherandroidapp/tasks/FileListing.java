package com.epubsearcherandroidapp.tasks;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxInputStream;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.epubsearcherandroidapp.activities.ListingActivity;
import com.epubsearcherandroidapp.util.EntryMetadata;

/**
 * @author Sa�l de Le�n
 * Task used to do the first call to dropbox api and list the root folder, or just all the epubs
 */
public class FileListing extends AsyncTask<Void, Long, Boolean> {

	private Context mContext;
	private ProgressDialog mDialog;
	private DropboxAPI<AndroidAuthSession> mDBApi;

	private FileOutputStream mFos;

	private boolean mCanceled;
	private Long mFileLen;
	private String mErrorMsg;
	private String path;
	private Boolean recMode;
	private Boolean titleMode;

	private HashMap<String, EntryMetadata> list;

	public FileListing(Context context, DropboxAPI<AndroidAuthSession> api, String dropboxPath, Boolean recMode, boolean titleMode) {
		mContext = context.getApplicationContext();

		mDBApi = api;

		path = dropboxPath;
		this.recMode = recMode;
		this.titleMode = titleMode;

		configureProgressDialog(context);
	}

	private void configureProgressDialog(Context context) {
		mDialog = new ProgressDialog(context);
		mDialog.setMessage("Descargando listado de archivos...");
		mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mCanceled = true;
				mErrorMsg = "Cancelado";
				if (mFos != null) {
					try {
						mFos.close();
					} catch (IOException e) {
					}
				}
			}
		});

		mDialog.show();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			if (mCanceled) {
				return false;
			}
			HashMap<String, EntryMetadata> files = listingFolderPath(path, recMode);
			setList(files);
			if (mCanceled) {
				return false;
			}
			return true;
		} catch (DropboxUnlinkedException e) {
			// The AuthSession wasn't properly authenticated or user unlinked.
		} catch (DropboxPartialFileException e) {
			// We canceled the operation
			mErrorMsg = "Listing canceled";
		} catch (DropboxServerException e) {
			// Server-side exception. These are examples of what could happen,
			// but we don't do anything special with them here.
			if (e.error == DropboxServerException._304_NOT_MODIFIED) {
				// won't happen since we don't pass in revision with metadata
			} else if (e.error == DropboxServerException._401_UNAUTHORIZED) {
				// Unauthorized, so we should unlink them. You may want to
				// automatically log the user out in this case.
			} else if (e.error == DropboxServerException._403_FORBIDDEN) {
				// Not allowed to access this
			} else if (e.error == DropboxServerException._404_NOT_FOUND) {
				// path not found (or if it was the thumbnail, can't be
				// thumbnailed)
			} else if (e.error == DropboxServerException._406_NOT_ACCEPTABLE) {
				// too many entries to return
			} else if (e.error == DropboxServerException._415_UNSUPPORTED_MEDIA) {
				// can't be thumbnailed
			} else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
				// user is over quota
			} else {
				// Something else
			}
			// This gets the Dropbox error, translated into the user's language
			mErrorMsg = e.body.userError;
			if (mErrorMsg == null) {
				mErrorMsg = e.body.error;
			}
		} catch (DropboxIOException e) {
			// Happens all the time, probably want to retry automatically.
			mErrorMsg = "Network error.  Try again.";
		} catch (DropboxParseException e) {
			// Probably due to Dropbox server restarting, should retry
			mErrorMsg = "Dropbox error.  Try again.";
		} catch (DropboxException e) {
			// Unknown error
			mErrorMsg = "Unknown error.  Try again.";
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected void onProgressUpdate(Long... progress) {
		int percent = (int) (100.0 * (double) progress[0] / mFileLen + 0.5);
		mDialog.setProgress(percent);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		mDialog.dismiss();
		if (result) {
			goToListingActivity();
		} else {
			// Error si no se pudo listar
			showToast(mErrorMsg);
		}
	}

	/**
	 * Calls to the listingActivity Activity
	 */
	private void goToListingActivity() {
		Intent activityList = new Intent(mContext, ListingActivity.class);
		ListingActivity.mDBApi = mDBApi;
		Bundle b = new Bundle();
		b.putSerializable("listado", this.list);
		b.putBoolean("titleMode", this.titleMode);
		activityList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activityList.putExtras(b);
		mContext.startActivity(activityList);
	}

	private void showToast(String msg) {
		Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
		error.show();
	}

	
	/**
	 * 
	 * @param path the path to seek
	 * @param rec tells if is a recursive search or not
	 * @return all files/directories in path folder
	 * @throws DropboxException
	 * @throws ParseException
	 * @throws IOException
	 */
	private HashMap<String, EntryMetadata> listingFolderPath(String path, boolean rec) 
			throws DropboxException, ParseException, IOException {
		HashMap<String, EntryMetadata> pathEntries = new HashMap<String, EntryMetadata>();
		Entry dirent = mDBApi.metadata(path, 1000, null, true, null);
		if (!rec) {
			// modo iterativo
			if (dirent.isDir) {
				// folder
				for (Entry ent : dirent.contents) {
					if (ent.path.endsWith(".epub") || ent.isDir) {
						if (ent.path.endsWith(".epub") && !ent.isDir) {
							// Extract title
							DropboxInputStream info = mDBApi.getFileStream(ent.path, null);
							Book book = new EpubReader().readEpub((InputStream) info);
							EntryMetadata e = new EntryMetadata(ent.path, ent.fileName(), ent.modified, ent.isDir);
							if (this.titleMode) {
								if (book.getMetadata().getTitles().get(0) != null) {
									e.setName(book.getMetadata().getTitles().get(0));
								}
							}
							pathEntries.put(ent.path, e);
							info.close();
						} else {
							EntryMetadata e = new EntryMetadata(ent.path, ent.fileName(), ent.modified, ent.isDir);
							pathEntries.put(ent.path, e);
						}
					}
				}
				return pathEntries;
			}
		} else {
			// modo recursivo
			pathEntries = getEpubFilesRec(dirent, new HashMap<String, EntryMetadata>());
		}
		return pathEntries;
	}

	
	/**
	 * @param dirent the actual folder
	 * @param files files/directories in the {@link}dirent folder
	 * @return all epub files in path folder
	 * @throws DropboxException
	 * @throws ParseException
	 * @throws IOException
	 */
	private HashMap<String, EntryMetadata> getEpubFilesRec(Entry dirent, HashMap<String, EntryMetadata> files) 
			throws DropboxException, ParseException, IOException {
		// Listamos todos los epub que hay en el directorio raiz y
		// subdirectorios
		if (dirent.contents != null) {
			for (Entry ent : dirent.contents) {
				if (ent.isDir) {
					Entry direntIn = mDBApi.metadata(ent.path, 0, null, true, null);
					files.putAll(getEpubFilesRec(direntIn, new HashMap<String, EntryMetadata>()));
				} else {
					if (ent.path.endsWith(".epub")) {
						// Extract title
						DropboxInputStream info = mDBApi.getFileStream(ent.path, null);
						Book book = new EpubReader().readEpub((InputStream) info);
						EntryMetadata e = new EntryMetadata(ent.path, ent.fileName(), ent.modified, ent.isDir);
						if (this.titleMode) {
							if (book.getMetadata().getTitles().get(0) != null) {
								e.setName(book.getMetadata().getTitles().get(0));
							}
						}
						files.put(ent.path, e);
						info.close();
					}
				}
			}
		}
		return files;
	}

	public HashMap<String, EntryMetadata> getList() {
		return list;
	}

	public void setList(HashMap<String, EntryMetadata> files) {
		this.list = files;
	}

}
