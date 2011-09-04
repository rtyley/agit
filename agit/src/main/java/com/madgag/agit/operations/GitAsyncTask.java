/*
 * Copyright (c) 2011 Roberto Tyley
 *
 * This file is part of 'Agit' - an Android Git client.
 *
 * Agit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Agit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.madgag.agit.operations;

import android.os.Handler;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.madgag.agit.R;
import com.madgag.agit.operation.lifecycle.OperationLifecycleSupport;
import com.madgag.android.blockingprompt.PromptBroker;
import roboguice.util.RoboAsyncTask;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static android.R.drawable.stat_notify_error;
import static com.madgag.agit.R.string.execute_clone_button_label;
import static com.madgag.agit.R.string.operation_cancelled;
import static com.madgag.agit.R.string.operation_failed;
import static java.lang.System.currentTimeMillis;

public class GitAsyncTask extends RoboAsyncTask<OpNotification> implements ProgressListener<Progress> {

	public final static String TAG = "GAT";

    @Inject GitOperationExecutor operationExecutor;
	@Inject GitOperationScopeExecutor gitOperationScopeExecutor;
    @Inject Provider<PromptBroker> promptBrokerProvider;
	
	private final GitOperation operation;
	private final OperationLifecycleSupport lifecycleSupport;
	
	private long startTime;

	private Progress latestProgress;

    private final Runnable publishOnUIThreadRunnable = new Runnable() {
        public void run() { publishLatestProgress(); }
    };
	
	@Inject
	public GitAsyncTask(
            @Named("uiThread") Handler handler,
			@Assisted GitOperation operation,
			@Assisted OperationLifecycleSupport lifecycleSupport) {
        handler(handler);
		this.operation = operation;
		this.lifecycleSupport = lifecycleSupport;
	}
	
    @Override
    protected void onPreExecute() {
    	Log.d(TAG, "Starting onPreExecute "+operation+" handler="+handler);
		try {
			OpNotification notification = gitOperationScopeExecutor.call(operation, new OperationUIContext(this, promptBrokerProvider), new Callable<OpNotification>() {
				public OpNotification call() throws Exception {
					return new OpNotification(operation.getOngoingIcon(), operation.getTickerText(), operation.getActionTitle(), operation.getUrl().toString());
				}
			});
			lifecycleSupport.startedWith(notification);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    	startTime = currentTimeMillis();
    }

	public OpNotification call() throws Exception {
        return operationExecutor.call(operation, new OperationUIContext(this, promptBrokerProvider), true);
	}
	
	@Override
	protected void onSuccess(OpNotification opResult) {
		long duration=currentTimeMillis()-startTime;
		Log.d(TAG, "Completed in "+duration+" ms");
        lifecycleSupport.success(opResult);
		lifecycleSupport.completed(opResult);
	}

	private String string(int resId, java.lang.Object... formatArgs) {
		return context.getString(resId, formatArgs);
	}

    @Override
    protected void onException(Exception e) throws RuntimeException {
        boolean cancelled = operation.isCancelled();
        Log.e(TAG, "Examining exception "+e+" op "+operation+" cancelled="+cancelled, e);
		String headline = string(cancelled ? operation_cancelled : operation_failed, operation.getName());
        OpNotification notification =
            new OpNotification(stat_notify_error, headline, cancelled?operation.getUrl().toString():e.getMessage());
        lifecycleSupport.error(notification);
        lifecycleSupport.completed(notification);
    }

    // Called on background thread
	public void publish(Progress... values) {
		latestProgress = values[values.length-1];
        handler().post(publishOnUIThreadRunnable);
		Log.d(TAG, "Posted "+latestProgress);
	}
	
	protected void publishLatestProgress() {
		lifecycleSupport.publish(latestProgress);
		Log.d(TAG, "Called lifecycle publisher with "+latestProgress);
	}

	public GitOperation getOperation() {
		return operation;
	}

	public Future<Void> getFutureInUse() {
		return future;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+"["+operation+"]";
	}
}
