package com.madgag.agit;

import static android.R.drawable.stat_sys_download;
import static android.R.drawable.stat_sys_download_done;

import org.eclipse.jgit.errors.NotSupportedException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.Transport;

import android.util.Log;

public class Fetcher implements Action {
	
	public static final String TAG = "Fetcher";
	
	private final RemoteConfig remoteConfig;

	public Fetcher(RemoteConfig remoteConfig) {
		this.remoteConfig = remoteConfig;
    }
	
	public int getOngoingIcon() {
		return stat_sys_download;
	}

	public String getTickerText() {
		return "Fetching "+remoteConfig.getName() + remoteConfig.getURIs().get(0).toString();
	}
	
    
	public OpResult execute(RepositoryOperationContext repositoryOperationContext, ProgressListener<Progress> progressListener) {
		Transport transport=repositoryOperationContext.transportFor(remoteConfig);
		try {
			FetchResult r = transport.fetch(new MessagingProgressMonitor(progressListener), null);
			Log.i(TAG, "No error during fetch it seems... "+r);
		} catch (NotSupportedException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} finally {
			transport.close();
		}
		return new OpResult(stat_sys_download_done,"Fetch complete", "Fetched "+remoteConfig.getName(), remoteConfig.getURIs().get(0).toString());
		
//		try {
//			runFetch(remoteConfig);
//			return createCompletionNotification();
//		} catch (Exception e) {
//			Log.e(TAG, "FETCH BROKE!",e);
//			e.printStackTrace();
//			return createNotificationWith(
//	    			stat_notify_error,
//	    			"Fetch failed",
//	    			e.getMessage(),
//	    			remoteConfig.getURIs().get(0).toString());
//		}
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



    

}
