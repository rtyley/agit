package com.madgag.agit.operations;

import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.madgag.agit.guice.OperationScope;
import com.madgag.agit.guice.RepositoryScope;

public class GitOperationExecutor {

    private static final String TAG = "GOE";

	@Inject RepositoryScope repoScope;
    @Inject OperationScope operationScope;
    @Inject Injector injector;

    public OpNotification call(GitOperation operation, OperationUIContext operationUIContext, boolean interruptExistingOp) {
		repoScope.enterWithRepoGitdir(operation.getGitDir());

		try {
            if (!injector.getInstance(RepoOpRegistry.class).setCurrentOperation(operation, interruptExistingOp)) {
                return null; // it all feels a bit bad
            }

            operationScope.enterWithUIContext(operation, operationUIContext);
            try {
			    injector.injectMembers(operation);
			    return operation.executeAndRecordThread();
            } finally {
                operationScope.exit();
            }
		} catch (RuntimeException e) {
            Log.e(TAG, "Failed doing "+operation, e);
            throw e;
        } finally {
            Log.d(TAG, "Exiting call()");
			repoScope.exit();
		}
	}
}
