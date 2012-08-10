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

package com.madgag.agit.operations;

import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.madgag.agit.guice.OperationScope;
import com.madgag.agit.guice.RepositoryScope;
import com.madgag.android.jgit.HarmonyFixInflater;

public class GitOperationExecutor {

    static {
        HarmonyFixInflater.establishHarmoniousRepose();
    }

    private static final String TAG = "GOE";

    @Inject
    RepositoryScope repoScope;
    @Inject
    OperationScope operationScope;
    @Inject
    Injector injector;

    public OpNotification call(GitOperation operation, OperationUIContext operationUIContext,
                               boolean interruptExistingOp) throws Exception {
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
                Log.d(TAG, "Exiting op scope");
                operationScope.exit();
            }
        } finally {
            Log.d(TAG, "Exiting repo scope");
            repoScope.exit();
        }
    }
}
