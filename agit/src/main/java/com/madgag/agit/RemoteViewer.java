package com.madgag.agit;

import java.io.File;
import java.net.URISyntaxException;

import org.eclipse.jgit.transport.RemoteConfig;

import android.content.Intent;
import android.os.Bundle;

public class RemoteViewer extends RepositoryActivity {
    
    public static Intent remoteViewerIntentFor(File gitdir, RemoteConfig remote) {
		return new GitIntentBuilder("git.view.REMOTE").gitdir(gitdir).remote(remote).toIntent();
	}

	private static final String TAG = "RemoteViewer";
	@Override String TAG() { return TAG; }
	
	private RemoteConfig remote;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remote_view);
		
		try {
			remote = new RemoteConfig(repo().getConfig(), getIntent().getStringExtra("remote"));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	void updateUI() {
		// TODO Auto-generated method stub
		
	}
}
