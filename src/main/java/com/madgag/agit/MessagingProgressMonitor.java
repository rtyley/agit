package com.madgag.agit;

import org.eclipse.jgit.lib.ProgressMonitor;

import android.util.Log;

public class MessagingProgressMonitor implements ProgressMonitor, CancellationSignaller {
	
	public static final String GIT_OPERATION_PROGRESS_UPDATE = "git.operation.progress.update";

	public static final String TAG = "MessagingProgressMonitor";
	
	private boolean output;

	private long taskBeganAt;

	private String msg;

	private int lastWorked;

	private int totalWork;
	
	private boolean cancelled=false;

	public String myNiceStatusString;
	
	public Progress currentProgress;

	private final ProgressListener<Progress> progressListener;




	public Progress getCurrentProgress() {
		return currentProgress;
	}
	
	public MessagingProgressMonitor( ProgressListener<Progress> progressListener) {
		this.progressListener = progressListener;
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
			if ((cmp * 10 / totalWork) != (lastWorked * 10) / totalWork) {
				display(cmp);
				System.err.flush();
			}
		}
		lastWorked = cmp;
		output = true;
	}

	private void display(int cmp) {
		currentProgress=new Progress(msg, totalWork, cmp);
		progressListener.publish(currentProgress);
	}

}
