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

    public OpNotification call(GitOperation operation, ProgressListener<Progress> boo) {
		scope.enterWithRepoGitdir(operation.getGitDir());
		try {
			injector.injectMembers(operation);
			// create and access scoped objects
			try {
				return operation.execute(boo);
			} catch (RuntimeException e) {
				String eventTitle = "Error " + operation.getDescription();
				Log.e(TAG, "Banged out of call with : " + eventTitle, e);
				String detail = e.getMessage() == null ? e.toString() : e.getMessage();
				return new OpNotification(stat_notify_error, operation.getName() + " failed", eventTitle, detail);
			}

		} finally {
            Log.d(TAG, "Exiting call()");
			scope.exit();
		}
	}
}
