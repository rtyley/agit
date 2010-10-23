package com.madgag.agit;

import java.io.File;

import android.content.Intent;
import android.util.Log;

public class GitIntents {

	private static final String TAG="GitIntents";
	
	public static File gitDirFrom(Intent intent) {
		String gitdirString = intent.getStringExtra("gitdir");
		File gitdir=new File(gitdirString);
		Log.i(TAG, "gitdir for "+intent+" = "+gitdir.getAbsolutePath());
		return gitdir;
	}
}
