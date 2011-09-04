package com.madgag.agit.operations;

import android.util.Log;
import com.google.inject.Inject;
import com.madgag.agit.GitFetchService;
import com.madgag.agit.R;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import roboguice.inject.InjectResource;

import java.io.File;
import java.util.Collection;

import static android.R.drawable.stat_sys_download;
import static android.R.drawable.stat_sys_download_done;
import static com.madgag.agit.R.string.*;
import static com.madgag.agit.R.string.fetched_from_remote_on_repo;
import static com.madgag.agit.git.Repos.niceNameFor;
import static com.madgag.agit.git.Repos.uriForRemote;

public class Fetch extends GitOperation {
		
	public static final String TAG = "Fetch";

    private final Repository repository;
    private final String remote;
	private final String fetchUrl;
    private final Collection<RefSpec> toFetch;

	@Inject GitFetchService fetchService;
	@InjectResource(fetch) String opName;

    public Fetch(Repository repository, String remote) {
        this(repository, remote, null);
    }

	public Fetch(Repository repository, String remote, Collection<RefSpec> toFetch) {
        super(repository.getDirectory());
        this.repository = repository;
        this.remote = remote;
        this.toFetch = toFetch;
		fetchUrl = uriForRemote(repository, remote).toString();
	}
	
	public int getOngoingIcon() {
		return stat_sys_download;
	}
	
	public OpNotification execute() {
		Log.d(TAG, "start execute() : repository=" + repository+" remote="+remote);
		FetchResult r = fetchService.fetch(remote, toFetch);
		return new OpNotification(stat_sys_download_done, str_operationCompleted(), string(fetched_from_remote_on_repo, remote, niceNameFor(repository)), fetchUrl);
    }
	
	public String getName() {
		return opName;
	}

	public String getTickerText() {
		return string(fetching)+"...";
	}
	
	public String getActionTitle() {
		return string(fetching_from_remote_on_repo, remote, niceNameFor(repository));
	}

	public CharSequence getUrl() {
		return fetchUrl;
	}

	public File getGitDir() {
		return repository.getDirectory();
	}
}
