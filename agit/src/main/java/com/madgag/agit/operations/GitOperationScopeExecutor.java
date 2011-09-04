package com.madgag.agit.operations;

import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.madgag.agit.guice.OperationScope;
import com.madgag.agit.guice.RepositoryScope;

import java.util.concurrent.Callable;

public class GitOperationScopeExecutor {

    private static final String TAG = "GOSE";

	@Inject RepositoryScope repoScope;
    @Inject OperationScope operationScope;
    @Inject Injector injector;

    public <V> V call(GitOperation operation, OperationUIContext operationUIContext, Callable<V> callable) throws Exception {
		repoScope.enterWithRepoGitdir(operation.getGitDir());
		try {
            operationScope.enterWithUIContext(operation, operationUIContext);
            try {
			    injector.injectMembers(operation);
			    return callable.call();
            } finally {
                Log.d(TAG, "Exiting op scope");
                operationScope.exit();
            }
        } finally {
            Log.d(TAG, "Exiting repo scope");
			repoScope.exit();
		}
	}
}
