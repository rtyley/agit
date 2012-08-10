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
import com.google.inject.name.Named;
import com.madgag.agit.guice.RepositoryScoped;

import java.io.File;

@RepositoryScoped
public class RepoOpRegistry {

    private final static String TAG = "ROR";
    private
    @Inject
    @Named("gitdir")
    File gitdir;

    private GitOperation currentOperation;

    public synchronized boolean setCurrentOperation(GitOperation op, boolean interruptExistingOp) {
        if (currentOperation == null || currentOperation.isDone()) {
            Log.d(TAG, "Prior op for  " + gitdir + " : " + currentOperation + " - new op=" + op);
            currentOperation = op;
            return true;
        }
        if (interruptExistingOp) {
            Log.d(TAG, "Interrupting existing op for " + gitdir + " : " + currentOperation + " -> " + op);
            currentOperation.cancel();
            currentOperation = op;
            return true;
        }
        Log.d(TAG, "Won't interrupt existing op for " + gitdir + " : " + currentOperation);
        return false;
    }

    public synchronized GitOperation getCurrentOperation() {
        return currentOperation;
    }

    public void addRepoListener(ProgressListener<Progress> progressListener) {
    }
}
