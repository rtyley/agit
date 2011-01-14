package com.madgag.agit;

import static com.madgag.agit.GitIntents.repositoryFrom;

import java.io.File;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;

import android.os.Bundle;
import android.util.Log;

public class RepositoryActivity extends android.app.Activity {
	
    protected Repository repository;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		repository = repositoryFrom(getIntent());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (!gitdir().exists()) {
			Log.d("RA", "");
			finish();
		}
	}
	
	private File gitdir() {
		return repository.getDirectory();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RepositoryCache.close(repository);
	}
}
