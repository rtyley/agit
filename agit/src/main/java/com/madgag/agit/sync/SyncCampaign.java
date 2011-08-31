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

package com.madgag.agit.sync;

import android.content.SyncResult;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import com.madgag.agit.git.Repos;
import com.madgag.agit.operations.*;
import com.madgag.android.blockingprompt.RejectBlockingPromptService;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;

import java.io.File;

import static com.madgag.agit.git.Repos.knownRepos;
import static com.madgag.agit.git.Repos.remoteConfigFor;
import static java.util.Arrays.asList;
import static org.eclipse.jgit.lib.Constants.DEFAULT_REMOTE_NAME;
import static org.eclipse.jgit.lib.RepositoryCache.close;

public class SyncCampaign implements CancellationSignaller, Runnable {
    private static final String TAG = "SyncCampaign";

    @Inject Provider<RejectBlockingPromptService> rejectPrompts;
    @Inject GitOperationExecutor operationExecutor;

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

        for (File gitdir : knownRepos()) {
            if (cancelled)
                return;
            syncRepo(gitdir, operationUIContext);
        }
    }

    private void syncRepo(File gitdir, OperationUIContext operationUIContext) {
        Repository repository = null;
        try {
            repository = Repos.openRepoFor(gitdir);

            currentOperation = new Fetch(repository, DEFAULT_REMOTE_NAME);
            if (operationExecutor.call(currentOperation, operationUIContext, false)!=null) { //feels bery bad
                syncResult.stats.numUpdates++;
            }
        } catch (Exception e) {
            Log.w(TAG, "Problem with " + gitdir, e);
        } finally {
            if (repository!=null)
                close(repository);
        }
    }


    public void cancel() {
        cancelled = true;
        Log.d(TAG, "Cancelled campaign - currentOperation="+currentOperation);
        if (currentOperation!=null) {
            currentOperation.cancel();
        }
    }

    public boolean isCancelled() {
        return cancelled;
    }

}
