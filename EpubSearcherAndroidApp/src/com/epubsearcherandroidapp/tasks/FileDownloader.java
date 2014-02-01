package com.epubsearcherandroidapp.tasks;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.epub.EpubReader;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxInputStream;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.epubsearcherandroidapp.activities.EpubCoverFullscreenActivity;



public class FileDownloader extends AsyncTask<Void, Long, Boolean> {

	private Context mContext;
	private final ProgressDialog mDialog;
	private DropboxAPI<AndroidAuthSession> mDBApi;

	private FileOutputStream mFos;

	private boolean mCanceled;
	private Long mFileLen;
	private String mErrorMsg;
	private String path;
	private Bitmap coverImage;
	
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
		try {
			if (mCanceled) {
				return false;
			}
			
//			String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
//			String filename = "temp.epub";
//			String pathA = baseDir + File.separator + filename;
//			File file = new File(pathA);
//			FileOutputStream outputStream = new FileOutputStream(file);

			//DropboxFileInfo info = mDBApi.getFile(path, null, outputStream, null);
			DropboxInputStream info = mDBApi.getFileStream(path, null);
			
			//Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);
			if (mCanceled) {
				info.close();
				return false;
			}
			//InputStream is = mContext.getApplicationContext().getAssets().open(pathA);
			Book book = new EpubReader().readEpub((InputStream)info);
			Metadata metadata = book.getMetadata();
			coverImage = BitmapFactory.decodeStream(book.getCoverImage().getInputStream());
			String bookInfo = "：" + metadata.getAuthors() + "\n ：" + metadata.getPublishers() + "\n ：" 
					+ metadata.getDates() + "\n ：" + metadata.getTitles() + "\n ："
					+ metadata.getDescriptions() + "\n ：" + metadata.getLanguage() + "\n\n ：";
			Log.e("epublib", bookInfo);
			//logTableOfContents(book.getTableOfContents().getTocReferences(), 0);
			info.close();
		} catch (IOException e) {
			Log.e("epublib", e.getMessage());
		} catch (DropboxException e) {
			e.printStackTrace();
		}
		return true;
	}

//	private void logTableOfContents(List<TOCReference> tocReferences, int depth) {
//		if (tocReferences == null) {
//			return;
//		}
//		for (TOCReference tocReference : tocReferences) {
//			StringBuilder tocstring = new StringBuilder();
//			for (int i = 0; i < depth; i++) {
//				tocstring.append("\t");
//			}
//			HashMap<String, String> map = new HashMap<String, String>();
//			String k = tocstring.append(tocReference.getTitle()).toString();
//			ArrayList<HashMap<String, String>> list1 = new ArrayList<HashMap<String, String>>();
//			list1.add(map);
//			String t = k;
//			Log.i("epublib", tocstring.toString());
//			logTableOfContents(tocReference.getChildren(), depth + 1);
//
//		}
//	}
	
	private void goToShowCoverActivity() {
		Intent activityList = new Intent(mContext, EpubCoverFullscreenActivity.class);
		activityList.putExtra("portada", this.coverImage);
		activityList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(activityList);
	}
	
	private void showToast(String msg) {
		Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
		error.show();
	}
}
