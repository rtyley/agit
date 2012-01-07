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

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.madgag.agit.operation.lifecycle.OperationLifecycleSupport;
import com.madgag.android.blockingprompt.PromptBroker;
import roboguice.util.RoboAsyncTask;

import java.util.concurrent.Future;

import static android.R.drawable.stat_notify_error;
import static java.lang.System.currentTimeMillis;

public class GitAsyncTask extends RoboAsyncTask<OpNotification> implements ProgressListener<Progress> {

	public final static String TAG = "GAT";

    @Inject GitOperationExecutor operationExecutor;
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
            Context context,
            @Named("uiThread") Handler handler,
			@Assisted GitOperation operation,
			@Assisted OperationLifecycleSupport lifecycleSupport) {
        super(context);
        handler(handler);
		this.operation = operation;
		this.lifecycleSupport = lifecycleSupport;
	}
	
    @Override
    protected void onPreExecute() {
    	Log.d(TAG, "Starting onPreExecute "+operation+" handler="+handler);
    	lifecycleSupport.startedWith(new OpNotification(operation.getOngoingIcon(), operation.getTickerText(), operation.getShortDescription(), operation.getUrl().toString()));
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

    @Override
    protected void onException(Exception e) throws RuntimeException {
        String opName = operation.getName();
        boolean cancelled = operation.isCancelled();
        Log.e(TAG, "Examining exception "+e+" op "+operation+" cancelled="+cancelled, e);
        OpNotification notification =
                cancelled ?new OpNotification(stat_notify_error, opName +" cancelled", operation.getUrl().toString()):
                        new OpNotification(stat_notify_error, opName +" failed", e.getMessage());
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
