package com.madgag.agit;

import static java.lang.System.identityHashCode;

import java.io.File;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.RepositoryCache.FileKey;
import org.eclipse.jgit.util.FS;

import android.util.Log;

public class Repos {
	public static Repository openRepoFor(File gitdir) {
		try {
			Repository repo = RepositoryCache.open(FileKey.lenient(gitdir, FS.DETECTED),false);
			Log.d("REPO", "Opened "+describe(repo));
			return repo;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String describe(Repository repository) {
		return repository+" #"+identityHashCode(repository);
	}
}
