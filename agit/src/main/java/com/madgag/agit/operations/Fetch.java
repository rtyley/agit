package com.madgag.agit.operations;

import static android.R.drawable.stat_sys_download;
import static android.R.drawable.stat_sys_download_done;

import java.io.File;
import java.util.Collection;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;

import android.util.Log;

import com.google.inject.Inject;
import com.madgag.agit.GitFetchService;
import com.madgag.agit.Progress;
import com.madgag.agit.ProgressListener;

public class Fetch implements GitOperation {
		
	public static final String TAG = "Fetch";

    private final Repository repository;
    private final RemoteConfig remote;
    private final Collection<RefSpec> toFetch;

	@Inject GitFetchService fetchService;

    public Fetch(Repository repository, RemoteConfig remote) {
        this(repository, remote, null);
    }

	public Fetch(Repository repository, RemoteConfig remote, Collection<RefSpec> toFetch) {
        this.repository = repository;
        this.remote = remote;
        this.toFetch = toFetch;
    }
	
	public int getOngoingIcon() {
		return stat_sys_download;
	}

	public String getTickerText() {
		return "Fetching "+remote.getName() + " " + fetchUrl();
	}
	
	public OpNotification execute(ProgressListener<Progress> progressListener) {
		Log.d(TAG, "start execute() : repository=" + repository+" remote="+remote.getName());
		FetchResult r = fetchService.fetch(remote, toFetch, progressListener);
		return new OpNotification(stat_sys_download_done,"Fetch complete", "Fetched "+remote.getName(), fetchUrl());
    }
	
	public String getName() {
		return "Fetch";
	}
	
	public String getDescription() {
		return "fetching "+remote.getName() + " " + fetchUrl();
	}

	private String fetchUrl() {
		return remote.getURIs().get(0).toString();
	}

	public CharSequence getUrl() {
		return fetchUrl();
	}

	public String getShortDescription() {
		return "Fetching "+remote.getName();
	}

	public File getGitDir() {
		return repository.getDirectory();
	}
}
