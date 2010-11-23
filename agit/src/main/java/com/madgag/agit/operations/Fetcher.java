package com.madgag.agit.operations;

import static android.R.drawable.stat_sys_download;
import static android.R.drawable.stat_sys_download_done;

import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RemoteConfig;

import com.madgag.agit.GitOperation;
import com.madgag.agit.OpNotification;
import com.madgag.agit.Progress;
import com.madgag.agit.ProgressListener;
import com.madgag.agit.RepositoryOperationContext;

public class Fetcher implements GitOperation {
	
	public static final String TAG = "Fetcher";
	
	private final RemoteConfig remote;

	public Fetcher(RemoteConfig remoteConfig) {
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
	
//	@Override
//	Notification createOngoingNotification() {
//		Notification n = createNotificationWith(
//				stat_sys_download,
//				"Fetchin",
//				"Fetching "+remoteConfig.getName(),
//				remoteConfig.getURIs().get(0).toString());
//		n.contentView=fetchProgressNotificationRemoteView();
//		n.contentView.setTextViewText(R.id.status_text, "This text really should be gone...");
//		return n;
//	}
	

//    private RemoteViews fetchProgressNotificationRemoteView() {
//    	
//		RemoteViews remoteView=remoteViewWithLayout(R.layout.fetch_progress);
//		remoteView.setProgressBar(R.id.status_progress, 512, 128, true);
//		return remoteView;
//    }
//    
//    @Override
//    Notification createCompletionNotification() {
//    	return createNotificationWith(
//    			stat_sys_download_done,
//    			"Fetch complete",
//    			"Fetched "+remoteConfig.getName(),
//    			remoteConfig.getURIs().get(0).toString());
//    }

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
