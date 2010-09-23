package com.madgag.agit;

import org.eclipse.jgit.lib.ProgressMonitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MessagingProgressMonitor implements ProgressMonitor, CancellationSignaller {
	
	public static final String GIT_OPERATION_PROGRESS_UPDATE = "git.operation.progress.update";

	public static class Progress {
		final String msg;
		final int totalWork,totalCompleted;
		public Progress(String msg, int totalWork, int totalCompleted) {
			this.msg = msg;
			this.totalWork = totalWork;
			this.totalCompleted = totalCompleted;
		}
	}

	public static final String TAG = "MessagingProgressMonitor";
	
	private boolean output;

	private long taskBeganAt;

	private String msg;

	private int lastWorked;

	private int totalWork;
	
	private final Context context;
	private final int notificationId;
	private final Notification notification;
	private final NotificationManager notificationManager;
	
	private boolean cancelled=false;

	public String myNiceStatusString;
	
	public Progress currentProgress;




	public Progress getCurrentProgress() {
		return currentProgress;
	}
	
	public MessagingProgressMonitor(Context context,
			int notificationId,
			Notification notification,
			NotificationManager notificationManager) {
		this.context = context;
		this.notificationId = notificationId;
		this.notification = notification;
		this.notificationManager = notificationManager;
	}
	
	public void setCancelled() {
		cancelled=true;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public void beginTask(final String title, final int total) {
		Log.d(TAG, "started "+title+" total="+total);
		
		endTask();
		msg = title;
		lastWorked = 0;
		totalWork = total;
	}

	public void endTask() {
		if (output) {
			if (totalWork != UNKNOWN)
				display(totalWork);
			System.err.println();
		}
		output = false;
		msg = null;
	}



	public void start(int arg0) {}

	public void update(int completed) {
		final int cmp = lastWorked + completed;
		Log.d(TAG, "cmp "+cmp);
		
		if (totalWork == UNKNOWN) {
			display(cmp);
			System.err.flush();
		} else {
			if ((cmp * 100 / totalWork) != (lastWorked * 100) / totalWork) {
				display(cmp);
				System.err.flush();
			}
		}
		lastWorked = cmp;
		output = true;
	}

	private void display(int cmp) {
		currentProgress=new Progress(msg, totalWork, cmp);
		// Sending notification every time seems to be TOO MUCH
//		notification.contentView.setTextViewText(R.id.status_text, currentProgress.msg);
//		notification.contentView.setProgressBar(R.id.status_progress, currentProgress.totalWork, currentProgress.totalCompleted, false);
//		notificationManager.notify(notificationId, notification);
		
		context.sendBroadcast(new Intent(GIT_OPERATION_PROGRESS_UPDATE));
		Log.d(TAG, "broadcasted completed "+cmp);
	}

}
