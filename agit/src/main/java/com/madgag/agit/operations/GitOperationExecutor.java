package com.madgag.agit.operations;

import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.madgag.agit.Progress;
import com.madgag.agit.ProgressListener;
import com.madgag.agit.guice.RepositoryScope;

import static android.R.drawable.stat_notify_error;

public class GitOperationExecutor {

    private static final String TAG = "GOE";

	@Inject RepositoryScope scope;
    @Inject Injector injector;

    public OpNotification call(GitOperation operation, ProgressListener<Progress> progressListener) {
		scope.enterWithRepoGitdir(operation.getGitDir());
		try {
			injector.injectMembers(operation);
			return operation.execute(progressListener);
		} finally {
            Log.d(TAG, "Exiting call()");
			scope.exit();
		}
	}
}
