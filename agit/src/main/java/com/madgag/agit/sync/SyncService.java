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
import com.madgag.agit.Progress;
import com.madgag.agit.ProgressListener;
import com.madgag.agit.Repos;
import com.madgag.agit.blockingprompt.RejectBlockingPromptService;
import com.madgag.agit.operations.Fetch;
import com.madgag.agit.operations.GitOperationExecutor;
import com.madgag.agit.operations.OperationUIContext;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;

import java.io.File;

import static com.madgag.agit.Repos.knownRepos;
import static com.madgag.agit.Repos.remoteConfigFor;
import static java.util.Arrays.asList;
import static org.eclipse.jgit.lib.Constants.DEFAULT_REMOTE_NAME;
import static org.eclipse.jgit.lib.RepositoryCache.close;

public class SyncService {
    public static final String TAG = "SS";

    @Inject Provider<RejectBlockingPromptService> promptProvider;
    @Inject GitOperationExecutor operationExecutor;

    public void syncAll(SyncResult syncResult) {
        ProgressListener<Progress> b = new ProgressListener<Progress>() {
            public void publish(Progress... values) {
                Log.d(TAG, asList(values).toString());
            }
        };
        OperationUIContext operationUIContext = new OperationUIContext(b, promptProvider);

        for (File gitdir : knownRepos()) {
            Repository repository = null;
            try {
                repository = Repos.openRepoFor(gitdir);
                RemoteConfig remoteConfig = remoteConfigFor(repository, DEFAULT_REMOTE_NAME);
                operationExecutor.call(new Fetch(repository, remoteConfig), operationUIContext);
                syncResult.stats.numUpdates++;
            } catch (RuntimeException e) {
                Log.w(TAG,"Problem with "+gitdir,e);
            } finally {
                if (repository!=null)
                    close(repository);
            }
        }

    }
}
