package com.madgag.agit.operations;

import static android.R.drawable.stat_notify_error;
import static com.madgag.agit.Repos.describe;
import static java.lang.System.currentTimeMillis;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;

import android.os.AsyncTask;
import android.util.Log;

import com.madgag.agit.Progress;
import com.madgag.agit.ProgressListener;
import com.madgag.agit.RepositoryOperationContext;
import com.madgag.agit.operation.lifecycle.OperationLifecycleSupport;

public class GitAsyncTask extends AsyncTask<Void, Progress, OpNotification> implements ProgressListener<Progress> {
	
	public final static String TAG = "GAT";
	
	protected final RepositoryOperationContext repositoryOperationContext;
	private final GitOperation operation;
	private final OperationLifecycleSupport lifecycleSupport;
	
	private long startTime;
	
	public GitAsyncTask(RepositoryOperationContext repositoryOperationContext, GitOperation operation, OperationLifecycleSupport lifecycleSupport) {
		this.repositoryOperationContext = repositoryOperationContext;
		this.operation = operation;
		this.lifecycleSupport = lifecycleSupport;
	}
	
    @Override
    protected void onPreExecute() {
    	Log.i(TAG, "Starting onPreExecute "+repositoryOperationContext);
    	lifecycleSupport.startedWith(new OpNotification(operation.getOngoingIcon(), operation.getTickerText(), "Event title", "Event detail"));
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
		
		closeRepoIfUsed();
		
		lifecycleSupport.completed(opResult);
	}

	private void closeRepoIfUsed() {
		Repository repository = operation.getRepository();
		Log.d(TAG, "Closing repo for : "+describe(repository));
		if (repository!=null) {
			RepositoryCache.close(repository);
		}
	}
	
	// Called on background thread
	public void publish(Progress... values) {
		publishProgress(values);
	}
	
	public GitOperation getOperation() {
		return operation;
	}
	
	// Called on UI thread
	@Override
	protected void onProgressUpdate(Progress... values) {
		Progress latestProgress=values[values.length-1];
		Log.i(TAG, "Got progress "+latestProgress);
		lifecycleSupport.publish(latestProgress);
	}

}
