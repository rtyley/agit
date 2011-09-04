package com.madgag.agit.operations;

import android.content.Context;
import android.util.Log;
import com.google.inject.Inject;
import com.madgag.agit.R;

import java.io.File;

import static com.madgag.agit.R.string.operation_complete;
import static com.madgag.agit.operations.GitOperation.Status.*;
import static java.lang.Thread.currentThread;

public abstract class GitOperation implements CancellationSignaller {


    protected @Inject Context context;

	public enum Status {
        NOT_STARTED,
        RUNNING,
        FINISHED
    }

    private Status status = NOT_STARTED;
    private boolean cancelled=false;
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

	public abstract String getActionTitle();
	
	public abstract CharSequence getUrl();

	public File getGitDir() {
        return gitdir;
    }

    public void cancel() {
        cancelled=true;
        if (executionThread!=null) {
            Log.d(TAG, "Interrupting "+executionThread+" due to cancel");
            executionThread.interrupt();
        }
    }
    
    public boolean isCancelled() {
        return cancelled;
    }

	protected String str_operationCompleted() {
		return string(operation_complete, getName());
	}

	protected String string(int resId, java.lang.Object... formatArgs) {
		return context.getString(resId, formatArgs);
	}
}
