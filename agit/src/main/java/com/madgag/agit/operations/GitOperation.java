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

import static com.madgag.agit.operations.GitOperation.Status.FINISHED;
import static com.madgag.agit.operations.GitOperation.Status.NOT_STARTED;
import static com.madgag.agit.operations.GitOperation.Status.RUNNING;
import static java.lang.Thread.currentThread;
import android.util.Log;

import java.io.File;

public abstract class GitOperation implements CancellationSignaller {

    public enum Status {
        NOT_STARTED,
        RUNNING,
        FINISHED
    }

    private Status status = NOT_STARTED;
    private boolean cancelled = false;
    protected final File gitdir;
    private Thread executionThread;
    private static final String TAG = "GO";

    public GitOperation(File gitdir) {
        this.gitdir = gitdir;
    }

    public abstract String getTickerText();

    public abstract int getOngoingIcon();

    public OpNotification executeAndRecordThread() throws Exception {
        executionThread = currentThread();
        status = RUNNING;
        try {
            return execute();
        } finally {
            status = FINISHED;
        }
    }

    public boolean isDone() {
        return status == FINISHED;
    }

    protected abstract OpNotification execute() throws Exception;

    public abstract String getName();

    public abstract String getShortDescription();

    public abstract String getDescription();

    public abstract CharSequence getUrl();

    public File getGitDir() {
        return gitdir;
    }

    public void cancel() {
        cancelled = true;
        if (executionThread != null) {
            Log.d(TAG, "Interrupting " + executionThread + " due to cancel");
            executionThread.interrupt();
        }
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
