package com.madgag.agit;

import static android.widget.Toast.*;
import static org.apache.commons.io.FileUtils.deleteDirectory;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class RepoDeleter implements Runnable {
	
	public static final String TAG = "RepoDeleter";
	
	public static final String REPO_DELETE_COMPLETED = "git.operation.repo.delete.completed";
	
	private final File gitdir;
	private final Context context;

	RepoDeleter(File gitdir, Context context) {
        this.gitdir = gitdir;
		this.context = context;
    }
	
    public void run() {
    	try {
    		File parent = gitdir.getParentFile();
    		Log.d(TAG, "Deleting : "+parent);
			deleteDirectory(parent);
			Log.d(TAG, "Deleted : "+parent);
			Uri gitdirUri = Uri.fromFile(gitdir);
			Intent deletionBroadcast = new Intent(REPO_DELETE_COMPLETED, gitdirUri);
			context.sendBroadcast(deletionBroadcast);
			Log.d(TAG, "Sent broadcast : "+gitdirUri.getAuthority());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
