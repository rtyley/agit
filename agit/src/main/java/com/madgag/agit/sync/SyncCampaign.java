/*
 * Copyright (c) 2011, 2012 Roberto Tyley
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
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

package com.madgag.agit.sync;

import static com.madgag.agit.git.Repos.refreshOperationFor;
import static java.util.Arrays.asList;
import static org.eclipse.jgit.lib.RepositoryCache.close;
import android.content.SyncResult;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import com.madgag.agit.db.RepoRecord;
import com.madgag.agit.db.ReposDataSource;
import com.madgag.agit.git.Repos;
import com.madgag.agit.operations.CancellationSignaller;
import com.madgag.agit.operations.GitOperation;
import com.madgag.agit.operations.GitOperationExecutor;
import com.madgag.agit.operations.OperationUIContext;
import com.madgag.agit.operations.Progress;
import com.madgag.agit.operations.ProgressListener;
import com.madgag.android.blockingprompt.RejectBlockingPromptService;

import java.io.File;

import org.eclipse.jgit.lib.Repository;

public class SyncCampaign implements CancellationSignaller, Runnable {
    private static final String TAG = "SyncCampaign";

    @Inject
    Provider<RejectBlockingPromptService> rejectPrompts;
    @Inject
    GitOperationExecutor operationExecutor;
    @Inject
    ReposDataSource reposDataSource;

    private final SyncResult syncResult;
    private GitOperation currentOperation;
    private boolean cancelled = false;

    @Inject
    public SyncCampaign(@Assisted SyncResult syncResult) {
        this.syncResult = syncResult;
    }

    public void run() {
        ProgressListener<Progress> progressListener = new ProgressListener<Progress>() {
            public void publish(Progress... values) {
                Log.d(TAG, asList(values).toString());
            }
        };
        OperationUIContext operationUIContext = new OperationUIContext(progressListener, rejectPrompts);

        for (RepoRecord repoRecord : reposDataSource.getAllRepos()) {
            if (cancelled)
                return;
            syncRepo(repoRecord.gitdir, operationUIContext);
        }
    }

    private void syncRepo(File gitdir, OperationUIContext operationUIContext) {
        Repository repository = null;
        try {
            repository = Repos.openRepoFor(gitdir);

            currentOperation = refreshOperationFor(repository);
            if (operationExecutor.call(currentOperation, operationUIContext, false) != null) { //feels bery bad
                syncResult.stats.numUpdates++;
            }
        } catch (Exception e) {
            Log.w(TAG, "Problem with " + gitdir, e);
        } finally {
            if (repository != null)
                close(repository);
        }
    }


    public void cancel() {
        cancelled = true;
        Log.d(TAG, "Cancelled campaign - currentOperation=" + currentOperation);
        if (currentOperation != null) {
            currentOperation.cancel();
        }
    }

    public boolean isCancelled() {
        return cancelled;
    }

}
