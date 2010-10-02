package com.madgag.agit;

import static android.app.Notification.FLAG_AUTO_CANCEL;
import static android.app.Notification.FLAG_ONGOING_EVENT;
import static java.lang.System.currentTimeMillis;

import org.connectbot.service.PromptHelper;

import android.app.Notification;
import android.os.AsyncTask;
import android.util.Log;

public abstract class GitOperation extends AsyncTask<Void, Progress, Void> implements ProgressListener<Progress> {
	
	public final String TAG = getClass().getSimpleName();
	
	protected final RepositoryOperationContext repositoryOperationContext;
	private long startTime;
	protected Notification ongoingNotification;
	protected PromptHelper promptHelper=new PromptHelper(TAG);
	
	public GitOperation(RepositoryOperationContext repositoryOperationContext) {
		this.repositoryOperationContext = repositoryOperationContext;
	}
	
    @Override
    protected void onPreExecute() {
    	startTime = currentTimeMillis();
    	ongoingNotification=createOngoingNotification();
    	ongoingNotification.flags = ongoingNotification.flags | FLAG_ONGOING_EVENT;
    	repositoryOperationContext.getService().startForeground(repositoryOperationContext.fetchOngoingId, ongoingNotification);
    }
    
	@Override
	protected void onPostExecute(Void v) {
		long duration=currentTimeMillis()-startTime;
		Log.i(getClass().getSimpleName(), "Completed in "+duration+" ms");
		repositoryOperationContext.getService().stopForeground(true); // Actually, we only want to call this if ALL threads are completed, I think...
		notifyCloneComplete();
	}
	
	
	
	private void notifyCloneComplete() {
		Notification completedNotification=createCompletionNotification();
		completedNotification.flags |= FLAG_AUTO_CANCEL;
		repositoryOperationContext.notifyCompletion(completedNotification);
	}
	
	abstract Notification createCompletionNotification();

	abstract Notification createOngoingNotification();

	public void publish(Progress... values) {
		publishProgress(values);
	}
	
	@Override
	protected void onProgressUpdate(Progress... values) {
		Progress p=values[values.length-1];
		Log.i(TAG, "Got prog "+p);
		ongoingNotification.contentView.setProgressBar(R.id.status_progress,p.totalWork,p.totalCompleted,p.isIndeterminate());
		ongoingNotification.contentView.setTextViewText(R.id.status_text, p.msg);
		repositoryOperationContext.notifyOngoing(ongoingNotification);
	}

}
