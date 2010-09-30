package com.madgag.agit;

import static android.app.Notification.FLAG_ONGOING_EVENT;
import static java.lang.System.currentTimeMillis;

import java.net.URISyntaxException;

import org.connectbot.service.PromptHelper;
import org.eclipse.jgit.errors.NotSupportedException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.Transport;

import android.app.Notification;
import android.util.Log;
import android.widget.RemoteViews;

public class Fetcher extends GitOperation {
	
	public static final String TAG = "Fetcher";
	private final Repository db;
	final MessagingProgressMonitor progressMonitor;
	public final PromptHelper promptHelper;
	private final RemoteConfig remoteConfig;
   
	public Fetcher(RemoteConfig remoteConfig, RepositoryOperationContext operationContext) {
		super(operationContext);
		db = operationContext.getRepository();
		this.remoteConfig = remoteConfig;
		this.promptHelper=new PromptHelper(db);
		progressMonitor = new MessagingProgressMonitor(this);
    }
    
    CancellationSignaller getCancellationSignaller() {
    	return progressMonitor;
    }
    
	@Override
	protected Void doInBackground(Void... arg0) {
		try {
			runFetch();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
	
	@Override
	Notification createOngoingNotification() {
		Notification n = new Notification(android.R.drawable.stat_sys_download, "Fetchin", currentTimeMillis());
		n.flags = n.flags | FLAG_ONGOING_EVENT;
		n.setLatestEventInfo(repositoryOperationContext.getService(), "Fetching "+remoteConfig.getName(), remoteConfig.getURIs().get(0).toString(), repositoryOperationContext.manageGitRepo);
		n.contentView=fetchProgressNotificationRemoteView();
		n.contentView.setTextViewText(R.id.status_text, "Hello, this message is in a custom expanded view");
		return n;
	}
	

    private RemoteViews fetchProgressNotificationRemoteView() {
		RemoteViews remoteView=new RemoteViews(repositoryOperationContext.getService().getApplicationContext().getPackageName(), R.layout.fetch_progress);
		remoteView.setProgressBar(R.id.status_progress, 512, 128, true);
		return remoteView;
    }
    
    @Override
    Notification createCompletionNotification() {
		Notification completedNotification=new Notification(android.R.drawable.stat_sys_download_done, "Fetch complete", currentTimeMillis());
		completedNotification.setLatestEventInfo(repositoryOperationContext.getService(), "Fetched "+remoteConfig.getName(), remoteConfig.getURIs().get(0).toString(), repositoryOperationContext.manageGitRepo);
		return completedNotification;
    }
    
	private FetchResult runFetch() throws NotSupportedException, URISyntaxException, TransportException {
		SshSessionFactory.setInstance(new AndroidSshSessionFactory(repositoryOperationContext,promptHelper));
		final Transport tn = Transport.open(db, remoteConfig);
		final FetchResult r;
		try {
			r = tn.fetch(progressMonitor, null);
		} finally {
			tn.close();
		}
		// showFetchResult(tn, r);
		Log.i(TAG, "Finished fetch "+r);
		return r;
	}
}
