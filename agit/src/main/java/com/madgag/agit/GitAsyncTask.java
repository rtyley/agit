package com.madgag.agit;

import static android.R.drawable.stat_notify_error;
import static java.lang.System.currentTimeMillis;
import android.app.Notification;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

public class GitAsyncTask extends AsyncTask<Void, Progress, OpNotification> implements ProgressListener<Progress> {
	
	public final String TAG = getClass().getSimpleName();
	
	protected final RepositoryOperationContext repositoryOperationContext;
	private final GitOperation operation;
	
	private long startTime;
	protected final Notification ongoingNotification;
	
	public GitAsyncTask(RepositoryOperationContext repositoryOperationContext, GitOperation operation) {
		this.repositoryOperationContext = repositoryOperationContext;
		this.operation = operation;
		ongoingNotification=createOngoingNotification();
	}
	
    @Override
    protected void onPreExecute() {
    	Log.i(TAG, "Starting onPreExecute "+repositoryOperationContext);
    	startTime = currentTimeMillis();
    }

	@Override
	protected OpNotification doInBackground(Void... params) {
		try {
			return operation.execute(repositoryOperationContext, this);
		} catch (RuntimeException e) {
			String eventTitle = "Error "+operation.getDescription();
			Log.e(TAG, eventTitle, e);
			String detail = e.getMessage()==null?e.toString():e.getMessage();
			return new OpNotification(stat_notify_error, operation.getName()+" failed", eventTitle, detail);
		}
	}
	
	@Override
	protected void onPostExecute(OpNotification opResult) {
		long duration=currentTimeMillis()-startTime;
		Log.i(TAG, "Completed in "+duration+" ms");
		Notification notification=repositoryOperationContext.createNotificationWith(opResult);
		repositoryOperationContext.notifyCompletion(notification);
	}
	
	private Notification createOngoingNotification() {
		Notification n = repositoryOperationContext.createNotificationWith(new OpNotification(operation.getOngoingIcon(), operation.getTickerText(), "Event title", "Event detail"));
		n.contentView = notificationView();
		return n;
	}
	
	private RemoteViews notificationView() {
		RemoteViews v=remoteViewWithLayout(R.layout.fetch_progress);
		v.setTextViewText(R.id.status_text, operation.getTickerText()); // TO-DO more suitable text?
		v.setProgressBar(R.id.status_progress,1,0,true);
		return v;
	}
	
	private RemoteViews remoteViewWithLayout(int layoutId) {
		return new RemoteViews(repositoryOperationContext.getService().getApplicationContext().getPackageName(), layoutId);
	}

	public Notification getOngoingNotification() {
		return ongoingNotification;
	}
	
	// Called on background thread
	public void publish(Progress... values) {
		publishProgress(values);
	}
	
	// Called on UI thread
	@Override
	protected void onProgressUpdate(Progress... values) {
		Progress p=values[values.length-1];
		Log.i(TAG, "Got prog "+p);
		RemoteViews view = ongoingNotification.contentView;
		view.setProgressBar(R.id.status_progress,p.totalWork,p.totalCompleted,p.isIndeterminate());
		view.setTextViewText(R.id.status_text, p.msg);
		repositoryOperationContext.notifyOngoing(ongoingNotification);
	}

}
