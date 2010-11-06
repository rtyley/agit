package com.madgag.agit;

import static java.lang.System.currentTimeMillis;

import org.connectbot.service.PromptHelper;

import android.app.Notification;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

public class GitOperation extends AsyncTask<Void, Progress, OpResult> implements ProgressListener<Progress> {
	
	public final String TAG = getClass().getSimpleName();
	
	protected final RepositoryOperationContext repositoryOperationContext;
	private final Action action;
	
	private long startTime;
	protected final Notification ongoingNotification;
	protected PromptHelper promptHelper=new PromptHelper(TAG);
	
	public GitOperation(RepositoryOperationContext repositoryOperationContext, Action action) {
		this.repositoryOperationContext = repositoryOperationContext;
		this.action = action;
		ongoingNotification=createOngoingNotification();
	}
	
    @Override
    protected void onPreExecute() {
    	Log.i(TAG, "Starting onPreExecute "+repositoryOperationContext);
    	startTime = currentTimeMillis();
    }

	@Override
	protected OpResult doInBackground(Void... params) {
		return action.execute(repositoryOperationContext, this);
	}
	
	@Override
	protected void onPostExecute(OpResult opResult) {
		long duration=currentTimeMillis()-startTime;
		Log.i(TAG, "Completed in "+duration+" ms");
		Notification notification=createNotificationWith(
				opResult.getDrawable(), opResult.getTickerText(), opResult.getEventTitle(), opResult.getEventDetail());
		repositoryOperationContext.notifyCompletion(notification);
	}
	
	private Notification createNotificationWith(int drawable, String tickerText, String eventTitle, String eventDetail) {
    	Notification n=new Notification(drawable, tickerText, currentTimeMillis());
		n.setLatestEventInfo(repositoryOperationContext.getService(), eventTitle, eventDetail, repositoryOperationContext.manageGitRepo);
		return n;
    }

	
	// abstract Notification createCompletionNotification();

	private Notification createOngoingNotification() {
		Notification n = createNotificationWith(action.getOngoingIcon(),action.getTickerText(),"Event title", "Event detail");
		n.contentView = notificationView();
		return n;
	}
	
	private RemoteViews notificationView() {
		RemoteViews v=remoteViewWithLayout(R.layout.fetch_progress);
		v.setTextViewText(R.id.status_text, action.getTickerText()); // TO-DO more suitable text?
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
