package com.madgag.agit;

import static org.apache.commons.io.FileUtils.deleteDirectory;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class RepoDeleter extends AsyncTask<Void, Void, Void> {
	
	public static final String TAG = "RepoDeleter";
	
	public static final String REPO_DELETE_COMPLETED = "git.operation.repo.delete.completed";
	
	private final File gitdir;
	private final Context context;

	RepoDeleter(File gitdir, Context context) {
        this.gitdir = gitdir;
		this.context = context;
    }
	
	@Override
	protected void onPreExecute () {
		// show 'deleting' dialog in RMA
	}
	
	@Override
	protected Void doInBackground(Void... args) {
    	try {
    		File parent = gitdir.getParentFile();
    		Log.d(TAG, "Deleting : "+parent);
			deleteDirectory(parent);
			Log.d(TAG, "Deleted : "+parent);
			// broadcastCompletion();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }
	
	@Override
    protected void onPostExecute(Void result) {
        // finish the RMA, which should wipe the progress bar on it as well.
    }

//	private void broadcastCompletion() {
//		Uri gitdirUri = Uri.fromFile(gitdir);
//		Intent deletionBroadcast = new Intent(REPO_DELETE_COMPLETED, gitdirUri);
//		context.sendBroadcast(deletionBroadcast);
//		Log.d(TAG, "Sent broadcast : "+gitdirUri.getAuthority());
//	}
	

}
