package com.madgag.agit;

import static android.R.drawable.stat_notify_error;
import static android.R.drawable.stat_sys_download;
import static android.R.drawable.stat_sys_download_done;

import org.connectbot.service.PromptHelper;
import org.eclipse.jgit.transport.RemoteConfig;

import android.app.Notification;
import android.util.Log;
import android.widget.RemoteViews;

public class Fetcher extends GitOperation {
	
	public static final String TAG = "Fetcher";
	
	private final RemoteConfig remoteConfig;
   
	public Fetcher(RemoteConfig remoteConfig, RepositoryOperationContext operationContext) {
		super(operationContext);
		this.remoteConfig = remoteConfig;
		this.promptHelper=new PromptHelper(operationContext.getRepository());
    }
    
    CancellationSignaller getCancellationSignaller() {
    	return progressMonitor;
    }
    
	@Override
	protected Notification doInBackground(Void... arg0) {
		try {
			runFetch(remoteConfig);
			return createCompletionNotification();
		} catch (Exception e) {
			Log.e(TAG, "FETCH BROKE!",e);
			e.printStackTrace();
			return createNotificationWith(
	    			stat_notify_error,
	    			"Fetch failed",
	    			e.getMessage(),
	    			remoteConfig.getURIs().get(0).toString());
		}
    }
	
	@Override
	Notification createOngoingNotification() {
		Notification n = createNotificationWith(
				stat_sys_download,
				"Fetchin",
				"Fetching "+remoteConfig.getName(),
				remoteConfig.getURIs().get(0).toString());
		n.contentView=fetchProgressNotificationRemoteView();
		n.contentView.setTextViewText(R.id.status_text, "This text really should be gone...");
		return n;
	}
	

    private RemoteViews fetchProgressNotificationRemoteView() {
    	
		RemoteViews remoteView=remoteViewWithLayout(R.layout.fetch_progress);
		remoteView.setProgressBar(R.id.status_progress, 512, 128, true);
		return remoteView;
    }
    
    @Override
    Notification createCompletionNotification() {
    	return createNotificationWith(
    			stat_sys_download_done,
    			"Fetch complete",
    			"Fetched "+remoteConfig.getName(),
    			remoteConfig.getURIs().get(0).toString());
    }
    

}
