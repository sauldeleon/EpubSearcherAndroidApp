package com.epubsearcherandroidapp.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.siegmann.epublib.domain.TOCReference;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.epubsearcherandroidapp.activities.ListingActivity;

public class FileDownloader extends AsyncTask<Void, Long, Boolean> {

	private Context mContext;
	private final ProgressDialog mDialog;
	private DropboxAPI<AndroidAuthSession> mDBApi;

	private FileOutputStream mFos;

	private boolean mCanceled;
	private Long mFileLen;
	private String mErrorMsg;
	private String path;
	
	public FileDownloader(Context context, DropboxAPI<AndroidAuthSession> api, String epubPath) {
		mContext = context.getApplicationContext();

		mDBApi = api;
		
		path = epubPath;
		mDialog = new ProgressDialog(context);
		mDialog.setMessage("Descargando portada...");
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
	protected void onPostExecute(Boolean result) {
		mDialog.dismiss();
		if (result) {
			goToShowCoverActivity();
		} else {
			// Error si no se pudo listar
			showToast(mErrorMsg);
		}
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Long... progress) {
		int percent = (int) (100.0 * (double) progress[0] / mFileLen + 0.5);
		mDialog.setProgress(percent);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
//		try {
//			if (mCanceled) {
//				return false;
//			}
//			
//			String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
//			String filename = "prueba.epub";
//			File file = new File(baseDir + File.separator + filename);
//			FileOutputStream outputStream = new FileOutputStream(file);
//
//			DropboxAPI<AndroidAuthSession> mDBApi2 = mDBApi;
//			DropboxFileInfo info = mDBApi2.getFile(path, null, outputStream, null);
//			Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);
//			if (mCanceled) {
//				return false;
//			}
//			InputStream is = getAssets().open(path);
//			Book book = new EpubReader().readEpub(is);
//			Metadata metadata = book.getMetadata();
//			String bookInfo = "：" + metadata.getAuthors() + "\n ：" + metadata.getPublishers() + "\n ：" + metadata.getDates() + "\n ：" + metadata.getTitles() + "\n ："
//					+ metadata.getDescriptions() + "\n ：" + metadata.getLanguage() + "\n\n ：";
//			Log.e("epublib", bookInfo);
//			logTableOfContents(book.getTableOfContents().getTocReferences(), 0);
//
//		} catch (IOException e) {
//			Log.e("epublib", e.getMessage());
//		} catch (DropboxException e) {
//			e.printStackTrace();
//		}
		return true;
	}

	/*private void logTableOfContents(List<TOCReference> tocReferences, int depth) {
		if (tocReferences == null) {
			return;
		}
		for (TOCReference tocReference : tocReferences) {
			StringBuilder tocstring = new StringBuilder();
			for (int i = 0; i < depth; i++) {
				tocstring.append("\t");
			}
			HashMap<String, String> map = new HashMap<String, String>();
			String k = tocstring.append(tocReference.getTitle()).toString();
			ArrayList<HashMap<String, String>> list1 = new ArrayList<HashMap<String, String>>();
			list1.add(map);
			String t = k;
			Log.i("epublib", tocstring.toString());
			logTableOfContents(tocReference.getChildren(), depth + 1);

		}
	}*/
	
	private void goToShowCoverActivity() {
		Intent activityList = new Intent(mContext, ListingActivity.class);
		ListingActivity.mDBApi = mDBApi;
		Bundle b = new Bundle();
		//b.putSerializable("listado", this.list);
		activityList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activityList.putExtras(b);
		mContext.startActivity(activityList);
	}
	
	private void showToast(String msg) {
		Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
		error.show();
	}
}
