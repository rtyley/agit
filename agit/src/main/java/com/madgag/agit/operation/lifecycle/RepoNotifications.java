package com.madgag.agit.operation.lifecycle;

import static android.app.Notification.FLAG_AUTO_CANCEL;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.madgag.agit.RepositoryManagementActivity.manageRepoPendingIntent;
import static java.lang.System.currentTimeMillis;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.madgag.agit.RepoOpScoped;
import com.madgag.agit.operations.OpNotification;

@RepoOpScoped
public class RepoNotifications {

	private static final String TAG="RN";
	
	private final Context context;
	private final NotificationManager notificationManager;
	private final int ongoingOpNotificationId, promptNotificationId, completionNotificationId;
	private final PendingIntent manageGitRepo;
	
	@Inject
	public RepoNotifications(Context context, @Named("gitdir") File gitdir, PendingIntent manageGitRepo) {
		this.context = context;
		this.manageGitRepo = manageGitRepo;
		this.ongoingOpNotificationId = gitdir.hashCode();
		this.completionNotificationId = ongoingOpNotificationId + 1;
		this.promptNotificationId = completionNotificationId + 1;
		notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
	}

	public Notification createNotificationWith(OpNotification opn) {
		Notification n = new Notification(opn.getDrawable(), opn.getTickerText(), currentTimeMillis());
		n.setLatestEventInfo(context, opn.getEventTitle(), opn.getEventDetail(), manageGitRepo);
		return n;
	}

	public void cancelOngoingNotification() {
		notificationManager.cancel(ongoingOpNotificationId);
	}

	public void clearPromptNotification() {
		notificationManager.cancel(promptNotificationId);
	}
	
	public void notifyPromptWith(OpNotification opNotification) {
		Notification n = createNotificationWith(opNotification);
		n.flags |= FLAG_AUTO_CANCEL;
		notificationManager.notify(promptNotificationId, n);
	}
	
	public void notifyCompletionWith(OpNotification completionNotification) {
		Log.i(TAG, "notifyCompletion() " + this + " : " + completionNotification);
		Notification cn = createNotificationWith(completionNotification);
		cn.flags |= FLAG_AUTO_CANCEL;
		notificationManager.notify(completionNotificationId, cn);
	}

	public void notifyOngoing(Notification ongoingNotification) {
		notificationManager.notify(ongoingOpNotificationId, ongoingNotification);
	}

	public int getOngoingNotificationId() {
		return ongoingOpNotificationId;
	}

	
}