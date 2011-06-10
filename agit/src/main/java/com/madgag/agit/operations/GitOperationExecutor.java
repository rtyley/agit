package com.madgag.agit.operations;

import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.madgag.agit.guice.OperationScope;
import com.madgag.agit.guice.RepositoryScope;

public class GitOperationExecutor {

    private static final String TAG = "GOE";

	@Inject RepositoryScope scope;
    @Inject OperationScope operationScope;
    @Inject Injector injector;

    public OpNotification call(GitOperation operation, OperationUIContext operationUIContext) {
		scope.enterWithRepoGitdir(operation.getGitDir());
		try {
            operationScope.enterWithUIContext(operationUIContext);
            try {
			    injector.injectMembers(operation);
			    return operation.execute();
            } finally {
                operationScope.exit();
            }
		} catch (RuntimeException e) {
            Log.e(TAG, "Failed doing "+operation, e);
            throw e;
        } finally {
            Log.d(TAG, "Exiting call()");
			scope.exit();
		}
	}
}
