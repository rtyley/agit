package com.madgag.agit;

import static com.madgag.agit.Repos.openRepoFor;

import java.io.File;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;

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
	
	public static String tagNameFrom(Intent intent) {
		return intent.getStringExtra("tag");
	}

	public static void addDirectoryTo(Intent intent, File directory) {
		intent.putExtra("directory", directory.getAbsolutePath());
	}	
	
	public static void addGitDirTo(Intent intent, File gitdir) {
		intent.putExtra("gitdir", gitdir.getAbsolutePath());
	}
	
	public static Repository repositoryFrom(Intent intent) {
		return openRepoFor(gitDirFrom(intent));
	}

	public static ObjectId commitIdFrom(Intent intent) {
		return ObjectId.fromString(intent.getStringExtra("commit"));
	}


}
