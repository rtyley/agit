package com.madgag.agit.operation.lifecycle;

import android.app.Notification;
import android.app.Service;
import android.util.Log;
import android.widget.RemoteViews;
import com.madgag.agit.R;
import com.madgag.agit.operations.OpNotification;
import com.madgag.agit.operations.Progress;

import static android.app.Notification.FLAG_ONGOING_EVENT;

// Stateful? Relates to a specific operation?
public class LongRunningServiceLifetime implements OperationLifecycleSupport {

	public final static String TAG = "LRSL";

	private final RepoNotifications repoNotifications;
	private final Service service;

	private Notification ongoingNotification;
    private StatusBarProgressView statusBarProgressView;

	public LongRunningServiceLifetime(RepoNotifications repoNotifications, Service service) {
		this.repoNotifications = repoNotifications;
		this.service = service;
	}

	public void startedWith(OpNotification startNotification) {
		ongoingNotification = repoNotifications.createNotificationWith(startNotification);
		ongoingNotification.flags = ongoingNotification.flags | FLAG_ONGOING_EVENT;
		ongoingNotification.contentView = notificationView(startNotification);
        statusBarProgressView = new StatusBarProgressView(ongoingNotification.contentView);
		foregroundServiceWith(ongoingNotification); //definitely the job of this class, right?!
	}

	public void publish(Progress p) {
		Log.i(TAG, "Publishing " + p);
        statusBarProgressView.publish(p);
		repoNotifications.notifyOngoing(ongoingNotification);
	}

    public void error(OpNotification errorNotification) {
    }

    public void success(OpNotification successNotification) {
    }

    public void completed(OpNotification completionNotification) {
		removeServiceFromForeground();
		repoNotifications.cancelOngoingNotification();
		repoNotifications.notifyCompletionWith(completionNotification);
	}

	private RemoteViews notificationView(OpNotification startNotification) {
		RemoteViews v=remoteViewWithLayout(R.layout.operation_progress);
		v.setTextViewText(R.id.operation_description, startNotification.getEventTitle()); // TO-DO more suitable text?
		v.setTextViewText(R.id.operation_long_url, startNotification.getEventDetail());
		v.setTextViewText(R.id.status_text, "Please wait...");
		v.setProgressBar(R.id.status_progress,1,0,true);
		return v;
	}
	
    private RemoteViews remoteViewWithLayout(int layoutId) {
        return new RemoteViews(service.getApplicationContext().getPackageName(), layoutId);
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
