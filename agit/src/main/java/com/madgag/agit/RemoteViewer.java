package com.madgag.agit;

import java.io.File;
import java.net.URISyntaxException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.transport.RemoteConfig;

import android.content.Intent;
import android.os.Bundle;

public class RemoteViewer extends android.app.Activity {
    
    public static Intent remoteViewerIntentFor(File gitdir, RemoteConfig remote) {
		return new GitIntentBuilder("git.view.REMOTE").gitdir(gitdir).remote(remote).toIntent();
	}

	private static final String TAG = "RemoteViewer";
	
    private Repository repository;
	
	private RemoteConfig remote;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remote_view);
		
		repository = GitIntents.repositoryFrom(getIntent());
		try {
			remote = new RemoteConfig(repository.getConfig(), getIntent().getStringExtra("remote"));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		RepositoryCache.close(repository);
	}
}
