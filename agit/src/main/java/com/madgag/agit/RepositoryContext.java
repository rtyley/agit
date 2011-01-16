package com.madgag.agit;

import static com.google.common.collect.Lists.newArrayList;
import static com.madgag.agit.GitIntents.repositoryFrom;
import static com.madgag.agit.Repos.describe;

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

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

public class RepositoryContext implements IndexChangedListener, RefsChangedListener {

	private final Activity activity;
	private final String tag;
    private final Repository repository;
	private final List<ListenerHandle> listeners = newArrayList();

    private final Handler handler = new Handler();
    
    private final Runnable onContentChangeRunnable = new Runnable() {
		public void run() { activity.onContentChanged(); }
	};

	public RepositoryContext(Activity activity, String tag) {
		this.activity = activity;
		this.tag = tag;
		repository = repositoryFrom(activity.getIntent());
	}
	
	public void onResume() {
		if (!gitdir().exists()) {
			Log.d(tag, "Finishing activity as gitdir gone : "+gitdir());
			activity.finish();
			return;
		}
		addListeners();
		activity.onContentChanged();
	}

	public void onPause() {
		removeListeners();
	}

	public void onDestroy() {
		RepositoryCache.close(repository);
	}
	
	private void removeListeners() {
		Log.d(tag, "Removing listeners for " + describe(repository));
		for (ListenerHandle handle : listeners) {
			handle.remove();
		}
		listeners.clear();
	}
	
	private void addListeners() {
		removeListeners();
		Log.d(tag, "Adding listeners for "+describe(repository));
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
		handler.post(onContentChangeRunnable);
	}

	public void onRefsChanged(RefsChangedEvent event) {
		handler.post(onContentChangeRunnable);
	}

}
