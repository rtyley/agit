package com.madgag.agit.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.madgag.agit.CancellationSignaller;
import com.madgag.agit.Progress;
import com.madgag.agit.ProgressListener;
import com.madgag.agit.Repos;
import com.madgag.agit.blockingprompt.RejectBlockingPromptService;
import com.madgag.agit.operations.Fetch;
import com.madgag.agit.operations.GitOperationExecutor;
import com.madgag.agit.operations.OperationUIContext;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;
import roboguice.inject.ContextScope;

import java.io.File;

import static com.madgag.agit.Repos.knownRepos;
import static com.madgag.agit.Repos.remoteConfigFor;
import static java.util.Arrays.asList;
import static org.eclipse.jgit.lib.Constants.DEFAULT_REMOTE_NAME;
import static org.eclipse.jgit.lib.RepositoryCache.close;

@Singleton
class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "AgitSync";
    
    @Inject Provider<RejectBlockingPromptService> rejectPrompts;
    @Inject GitOperationExecutor operationExecutor;
    @Inject ContextScope contextScope;
//    private CancellationSignaller currentSyncCancellationSignaller;

    @Inject
    public SyncAdapter(Context context) {
        super(context, true);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Context context = getContext();
        contextScope.enter(context);
        try {
            syncWith(account, syncResult);
        } finally {
            contextScope.exit(context);
        }
    }

    private void syncWith(Account account, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync account=" + account);
        //currentSyncCancellationSignaller = new;
        ProgressListener<Progress> progressListener = new ProgressListener<Progress>() {
            public void publish(Progress... values) {
                Log.d(TAG, asList(values).toString());
            }
        };
        OperationUIContext operationUIContext = new OperationUIContext(progressListener, rejectPrompts);

        for (File gitdir : knownRepos()) {
//            if (currentSyncCancellationSignaller.isCancelled()) {
//                return;
//            }
            syncRepo(gitdir, operationUIContext, syncResult);
        }
    }

    private void syncRepo(File gitdir, OperationUIContext operationUIContext, SyncResult syncResult) {
        Repository repository = null;
        try {
            repository = Repos.openRepoFor(gitdir);
            RemoteConfig remoteConfig = remoteConfigFor(repository, DEFAULT_REMOTE_NAME);
            if (operationExecutor.call(new Fetch(repository, remoteConfig), operationUIContext, false)!=null) { //feels bery bad
                syncResult.stats.numUpdates++;
            }
        } catch (RuntimeException e) {
            Log.w(TAG, "Problem with " + gitdir, e);
        } finally {
            if (repository!=null)
                close(repository);
        }
    }

    @Override
    public void onSyncCanceled() {
//        if (currentSyncCancellationSignaller!=null) {
//            currentSyncCancellationSignaller.cancel();
//        }
    }

}
