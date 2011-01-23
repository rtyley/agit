package com.madgag.gravatar.android;

import static java.net.URLEncoder.encode;
import static java.util.Collections.synchronizedSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class GravatarProvider {

    private final static String ROOT_DIR = "gravatars";
    private final Set<String> gravitarIdsCurrentlyBeingFetched = synchronizedSet(new HashSet<String>());
	
    public Bitmap getGravatar(String gravatarId, int size) {
    	Log.d("GP", "getGravatar "+gravatarId);
        try {
            File imageFile = imageFileFor(gravatarId);
            Bitmap bm = BitmapFactory.decodeFile(imageFile.getPath());
            if (bm == null) {
                bm = downloadAndStore(gravatarId, imageFile);
            }
            return bm;
        } catch (IOException e) {
            Log.e("debug", "Error saving bitmap", e);
            return null;
        }
    }
    
	public Bitmap getGravatar(String gravatarId, GravatarLoadListener onSuccessfulDelayedFetch) {
		Log.d("GP", "getGravatar with possible delay : "+gravatarId);
		try {
			File imageFile = imageFileFor(gravatarId);
	        Bitmap bm = BitmapFactory.decodeFile(imageFile.getPath());
	        if (bm == null) {
	        	startFetchIfRequired(gravatarId, onSuccessfulDelayedFetch);
	        }
	        return bm;
		} catch (Exception e) {}
		return null;
	}

	private void startFetchIfRequired(String gravatarId, GravatarLoadListener onSuccessfulDelayedFetch) {
		boolean currentlyBeingFetched = gravitarIdsCurrentlyBeingFetched.contains(gravatarId);
		Log.d("GP", gravatarId+" currentlyBeingFetched = "+currentlyBeingFetched);
		if (!currentlyBeingFetched) {
			gravitarIdsCurrentlyBeingFetched.add(gravatarId);
			new AsyncGravatarFetchTask(gravatarId, onSuccessfulDelayedFetch).execute();
		}
	}


	private File imageFileFor(String gravatarId) throws IOException {
		final File gravatarDir = ensure_directory(ROOT_DIR);
		hideMediaFromGallery(gravatarDir);

		return new File(gravatarDir, gravatarId + ".png");
	}

	private Bitmap downloadAndStore(String gravatarId, File imageFile) throws IOException {
		Bitmap bm = downloadGravatar(gravatarId);
		bm.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(imageFile));
		return bm;
	}
    
    private Bitmap downloadGravatar(String gravatarId) throws IOException {
    	Log.d("GP", "downloadGravatar "+gravatarId);
        URL aURL = new URL("http://www.gravatar.com/avatar/" + encode(gravatarId) + "?s=100&d=mm");
        HttpURLConnection conn = (HttpURLConnection) aURL.openConnection();
        conn.setDoInput(true);
        conn.connect();
        InputStream is = conn.getInputStream();
        try {
        	return BitmapFactory.decodeStream(is);
        } finally {
        	is.close();
        }
    }

    private static File ensure_directory(final String path) throws IOException {
        File root = Environment.getExternalStorageDirectory();
        if (!root.canWrite()) {
            throw new IOException("External storage directory is not writable");
        }
        
        File gravatarCacheDir = new File(root, path);
		if (!gravatarCacheDir.exists() && !gravatarCacheDir.mkdirs()) {
            throw new IOException("Unable to create " + gravatarCacheDir);
        }
        return gravatarCacheDir;
    }

    private static void hideMediaFromGallery(final File gravatarDir) throws IOException {
        new File(gravatarDir, ".nomedia").createNewFile();
    }

    
    
	private class AsyncGravatarFetchTask extends AsyncTask<Void, Void, Bitmap> {

		private final GravatarLoadListener onSuccess;
		private final String gravatarId;

		AsyncGravatarFetchTask(String gravatarId, GravatarLoadListener onSuccess) {
			this.onSuccess = onSuccess;
			this.gravatarId = gravatarId;
		}
		
		@Override
		protected Bitmap doInBackground(Void... v) {
			try {
				return downloadAndStore(gravatarId, imageFileFor(gravatarId));
			} catch (IOException e) { return null; } finally {					
				gravitarIdsCurrentlyBeingFetched.remove(gravatarId);
				Log.d("AGFT", "Finished aysnc fetch : remaining ids size ="+gravitarIdsCurrentlyBeingFetched.size());
			}
		}
		
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			onSuccess.onSuccessfulLoad(bitmap);
		}
		
	}
}
