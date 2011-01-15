package com.madgag.agit;

import static com.google.common.collect.Lists.newArrayList;
import static com.madgag.agit.GitIntents.repositoryFrom;
import static java.lang.System.identityHashCode;

import java.io.File;
import java.util.List;

import org.eclipse.jgit.events.IndexChangedEvent;
import org.eclipse.jgit.events.IndexChangedListener;
import org.eclipse.jgit.events.ListenerHandle;
import org.eclipse.jgit.events.ListenerList;
import org.eclipse.jgit.events.RefsChangedEvent;
import org.eclipse.jgit.events.RefsChangedListener;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public abstract class RepositoryActivity extends android.app.Activity implements IndexChangedListener, RefsChangedListener {

    private final Handler handler = new Handler();
    
    private final Runnable updateUIRunnable = new Runnable() {
		public void run() { updateUI(); }
	};
	
    private Repository repository;
	private final List<ListenerHandle> listeners = newArrayList();
	
	abstract String TAG();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		repository = repositoryFrom(getIntent());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (!gitdir().exists()) {
			Log.d(TAG(), "Finishing activity as gitdir gone : "+gitdir());
			finish();
			return;
		}
		addListeners();
		updateUI();
	}

	@Override
	protected void onPause() {
		super.onPause();
		removeListeners();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RepositoryCache.close(repository);
	}
	
	abstract void updateUI();
	
	private void removeListeners() {
		Log.d(TAG(), "Removing listeners for " + repoDescription());
		for (ListenerHandle handle : listeners) {
			handle.remove();
		}
		listeners.clear();
	}
	
	private void addListeners() {
		removeListeners();
		Log.d(TAG(), "Adding listeners for "+repoDescription());
		ListenerList listenerList = repository.getListenerList();
		listeners.add(listenerList.addIndexChangedListener(this));
		listeners.add(listenerList.addRefsChangedListener(this));
	}
	
	public Repository repo() {
		return repository;
	}
	
	public File gitdir() {
		return repository.getDirectory();
	}
	
	public void onIndexChanged(IndexChangedEvent event) {
		handler.post(updateUIRunnable);
	}

	public void onRefsChanged(RefsChangedEvent event) {
		handler.post(updateUIRunnable);
	}

	private String repoDescription() {
		return "a repo with "+identityHashCode(repository) + " " + repository.getDirectory();
	}
}
