package com.madgag.agit;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.RepositoryCache.FileKey;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.util.FS;

import android.content.Intent;
import android.util.Log;

public class GitIntents {

	private static final String TAG="GitIntents";
	
	public static File gitDirFrom(Intent intent) {
		String gitdirString = intent.getStringExtra("gitdir");
		Log.i(TAG, "gitdirString = "+gitdirString);
		File gitdir=new File(gitdirString);
		Log.i(TAG, "gitdir for "+intent+" = "+gitdir.getAbsolutePath());
		return gitdir;
	}

	public static String branchNameFrom(Intent intent) {
		return intent.getStringExtra("branch");
	}
	
	public static void addGitDirTo(Intent intent, File gitdir) {
		intent.putExtra("gitdir", gitdir.getAbsolutePath());
	}
	
	public static Repository repositoryFrom(Intent intent) {
		try {
			return RepositoryCache.open(FileKey.lenient(gitDirFrom(intent), FS.DETECTED));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


}
