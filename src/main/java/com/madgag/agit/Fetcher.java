package com.madgag.agit;

import static android.app.Notification.FLAG_AUTO_CANCEL;
import static android.app.Notification.FLAG_ONGOING_EVENT;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.madgag.agit.RepositoryManagementActivity.manageGitRepo;
import static java.lang.System.currentTimeMillis;

import java.io.File;
import java.net.URISyntaxException;

import org.connectbot.service.PromptHelper;
import org.eclipse.jgit.errors.NotSupportedException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.Transport;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

public class Fetcher extends AsyncTask<Void, Progress, FetchResult> implements ProgressListener<Progress> {
	
	public static final String TAG = "Fetcher";
	private final RepositoryOperationContext operationContext;
	private final Repository db;
	private final String remote;
	final MessagingProgressMonitor progressMonitor;
	public final PromptHelper promptHelper;
	public Notification notification;
	private NotificationManager notificationManager;
	private final Context context;
	private final Service service;
   
	public Fetcher(Repository db, String remote, Service service,RepositoryOperationContext operationContext) {
        this.db = db;
		this.remote = remote;
		this.service = service;
		this.context = service;
		this.operationContext = operationContext;
		this.promptHelper=new PromptHelper(db);
		notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		notification = ongoingFetchNotification(db.getDirectory(), remote);
		progressMonitor = new MessagingProgressMonitor(this);
    }
    
    CancellationSignaller getCancellationSignaller() {
    	return progressMonitor;
    }
    
    @Override
    protected void onPreExecute() {
    	service.startForeground(operationContext.fetchOngoingId, notification);
    }
    
	@Override
	protected FetchResult doInBackground(Void... arg0) {
		try {
			return runFetch();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
	
	private Notification ongoingFetchNotification(File gitdir, String remote) {
		Notification n = new Notification(R.drawable.diff_changetype_add, "Hello", currentTimeMillis());
		n.flags = n.flags | FLAG_ONGOING_EVENT;
		n.setLatestEventInfo(context, "Fetching "+remote, "Like a horse", manageGitRepo(gitdir,context));
		n.contentView=fetchProgressNotificationRemoteView();
		return n;
	}
	

    private RemoteViews fetchProgressNotificationRemoteView() {
		RemoteViews remoteView=new RemoteViews(context.getApplicationContext().getPackageName(), R.layout.fetch_progress);
		remoteView.setProgressBar(R.id.status_progress, 512, 128, true);
		return remoteView;
    }
    
	
	@Override
	protected void onPostExecute(FetchResult result) {
		service.stopForeground(true); // Actually, we only want to call this if ALL threads are completed, I think...
		notifyFetchComplete();
	}

	private void notifyFetchComplete() {
		// The user is not interested in old fetch Notifications if we've done a new one
		Notification completedNotification=new Notification(R.drawable.diff_changetype_modify, "Fetch complete", currentTimeMillis());
		completedNotification.setLatestEventInfo(context, "Fetched "+remote, "UTTERLY", manageGitRepo(db,context));
		completedNotification.flags |= FLAG_AUTO_CANCEL;
		notificationManager.notify(operationContext.fetchCompletionId, completedNotification);
	}
    
	private FetchResult runFetch() throws NotSupportedException, URISyntaxException, TransportException {
		SshSessionFactory.setInstance(new AndroidSshSessionFactory(promptHelper));
		final Transport tn = Transport.open(db, remote);
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

	@Override
	protected void onProgressUpdate(Progress... values) {
		Progress p=values[values.length-1];
		Log.i(TAG, "Got prog "+p);
	}

	public void publish(Progress... values) {
		publishProgress(values);
	}
}
