package com.madgag.agit;

import java.io.File;

import org.eclipse.jgit.lib.Repository;

import android.app.Activity;
import android.os.Bundle;

public abstract class RepositoryActivity extends Activity {
	
    private RepositoryContext rc;
	
	abstract String TAG();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rc = new RepositoryContext(this, TAG());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		rc.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		rc.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		rc.onDestroy();
	}
	
	public Repository repo() {
		return rc.repo();
	}
	
	public File gitdir() {
		return rc.gitdir();
	}
}
