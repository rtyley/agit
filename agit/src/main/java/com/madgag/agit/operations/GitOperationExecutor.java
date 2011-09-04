package com.madgag.agit.operations;

import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.madgag.agit.guice.OperationScope;
import com.madgag.agit.guice.RepositoryScope;

import java.util.concurrent.Callable;

public class GitOperationExecutor {

    private static final String TAG = "GOE";

	@Inject GitOperationScopeExecutor gitOperationScopeExecutor;
	@Inject Injector injector;

    public OpNotification call(final GitOperation operation,
							   OperationUIContext operationUIContext,
							   final boolean interruptExistingOp) throws Exception {
		return gitOperationScopeExecutor.call(operation, operationUIContext, new Callable<OpNotification>() {
			public OpNotification call() throws Exception {
				if (!injector.getInstance(RepoOpRegistry.class).setCurrentOperation(operation, interruptExistingOp)) {
					return null; // it all feels a bit bad
				}
				return operation.executeAndRecordThread();
			}
		});
	}
}
