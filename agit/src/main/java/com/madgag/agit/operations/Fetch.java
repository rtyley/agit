package com.madgag.agit.operations;

import static android.R.drawable.stat_sys_download;
import static android.R.drawable.stat_sys_download_done;

import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RemoteConfig;

import com.madgag.agit.Progress;
import com.madgag.agit.ProgressListener;
import com.madgag.agit.RepositoryOperationContext;

public class Fetch implements GitOperation {
	
	public static final String TAG = "Fetch";
	
	private final RemoteConfig remote;

	public Fetch(RemoteConfig remoteConfig) {
		this.remote = remoteConfig;
    }
	
	public int getOngoingIcon() {
		return stat_sys_download;
	}

	public String getTickerText() {
		return "Fetching "+remote.getName() + " " + fetchUrl();
	}
	
	public OpNotification execute(RepositoryOperationContext repositoryOperationContext, ProgressListener<Progress> progressListener) {
		FetchResult r = repositoryOperationContext.fetch(remote, progressListener);
		
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

}
