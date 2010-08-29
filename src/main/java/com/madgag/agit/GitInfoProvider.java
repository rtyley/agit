package com.madgag.agit;

import java.io.File;

import org.eclipse.jgit.lib.Constants;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class GitInfoProvider extends ContentProvider {
	public static final Uri CONTENT_URI  = Uri.parse("content://com.madgag.agit.gitinfoprovider/repos");
	
	public static final String TAG = "RepoInfoProvider";

	private File reposDir;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		reposDir = new File(Environment.getExternalStorageDirectory(),"git-repos");
		if (!reposDir.exists() && !reposDir.mkdirs()) {
			Log.e(TAG, "Could not create default repos dir : "+reposDir.getAbsolutePath());
			return false;
		}
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		MatrixCursor matrixCursor=new MatrixCursor(new String[]{"_id","gitdir"});
		for (File repoDir : reposDir.listFiles()) {
			File gitdir=new File(repoDir,Constants.DOT_GIT);
			if (gitdir.exists() && gitdir.isDirectory()) {
				String gitdirPath=gitdir.getAbsolutePath();
				Log.d(TAG, "Found gitish : "+gitdirPath);
				matrixCursor.newRow().add(gitdirPath.hashCode()).add(gitdirPath);
			}
		}
		return matrixCursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
