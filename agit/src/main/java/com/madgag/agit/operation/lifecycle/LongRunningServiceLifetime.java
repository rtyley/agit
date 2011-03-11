package com.madgag.agit.operation.lifecycle;

import static android.app.Notification.FLAG_ONGOING_EVENT;
import android.app.Notification;
import android.app.Service;
import android.util.Log;
import android.widget.RemoteViews;

import com.madgag.agit.Progress;
import com.madgag.agit.R;
import com.madgag.agit.operations.GitOperation;
import com.madgag.agit.operations.OpNotification;

// Stateful? Relates to a specific operation?
public class LongRunningServiceLifetime implements OperationLifecycleSupport {

	public final static String TAG = "LRSL";

	private final RepoNotifications repoNotifications;
	private final Service service;
	private final GitOperation gitOperation;

	private Notification ongoingNotification;

	public LongRunningServiceLifetime(RepoNotifications repoNotifications, Service service, GitOperation gitOperation) {
		this.repoNotifications = repoNotifications;
		this.service = service;
		this.gitOperation = gitOperation;
	}

	public void startedWith(OpNotification startNotification) {
		ongoingNotification = repoNotifications.createNotificationWith(startNotification);
		ongoingNotification.flags = ongoingNotification.flags | FLAG_ONGOING_EVENT;
		ongoingNotification.contentView = notificationView();
		foregroundServiceWith(ongoingNotification); //definitely the job of this class, right?!
	}

	public void publish(Progress p) {
		Log.i(TAG, "Publishing " + p);
		updateWithProgress(p, ongoingNotification.contentView);
		repoNotifications.notifyOngoing(ongoingNotification);
	}

	public void completed(OpNotification completionNotification) {
		removeServiceFromForeground();
		repoNotifications.cancelOngoingNotification();
		repoNotifications.notifyCompletionWith(completionNotification);
	}

	private RemoteViews notificationView() {
		RemoteViews v=remoteViewWithLayout(R.layout.fetch_progress);
		v.setTextViewText(R.id.operation_description, gitOperation.getShortDescription()); // TO-DO more suitable text?
		v.setTextViewText(R.id.operation_long_url, gitOperation.getUrl());
		v.setTextViewText(R.id.status_text, "Please wait...");
		v.setProgressBar(R.id.status_progress,1,0,true);
		return v;
	}
	
    private RemoteViews remoteViewWithLayout(int layoutId) {
        return new RemoteViews(service.getApplicationContext().getPackageName(), layoutId);
    }

	private void updateWithProgress(Progress p, RemoteViews view) {
		view.setProgressBar(R.id.status_progress, p.totalWork, p.totalCompleted, p.isIndeterminate());
		view.setTextViewText(R.id.status_text, p.msg);
	}

	private void foregroundServiceWith(Notification ongoingNotification) {
		Log.i(TAG, "Starting " + ongoingNotification + " in the foreground...");
		try {
			service.startForeground(repoNotifications.getOngoingNotificationId(), ongoingNotification);
		} catch (NullPointerException e) {
			Log.d(TAG, "startForeground NPE - see http://code.google.com/p/android/issues/detail?id=12117");
		}
	}
	
	private void removeServiceFromForeground() {
		try {
			// Actually, we only want to call this if ALL threads are completed
			service.stopForeground(true);
		} catch (NullPointerException e) {
			Log.d(TAG,"stopForeground NPE - see http://code.google.com/p/android/issues/detail?id=12117", e);
		}
	}
}
