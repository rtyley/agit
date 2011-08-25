package com.madgag.agit.operations;

import android.util.Log;
import com.google.inject.Inject;
import com.madgag.agit.GitFetchService;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;

import java.io.File;
import java.util.Collection;

import static android.R.drawable.stat_sys_download;
import static android.R.drawable.stat_sys_download_done;

public class Fetch extends GitOperation {
		
	public static final String TAG = "Fetch";

    private final Repository repository;
    private final RemoteConfig remote;
    private final Collection<RefSpec> toFetch;

	@Inject GitFetchService fetchService;

    public Fetch(Repository repository, RemoteConfig remote) {
        this(repository, remote, null);
    }

	public Fetch(Repository repository, RemoteConfig remote, Collection<RefSpec> toFetch) {
        super(repository.getDirectory());
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
	
	public OpNotification execute() {
		Log.d(TAG, "start execute() : repository=" + repository+" remote="+remote.getName());
		FetchResult r = fetchService.fetch(remote, toFetch);
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
