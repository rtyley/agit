package com.madgag.agit.operations;

import java.io.File;

import android.nfc.Tag;
import android.util.Log;
import com.madgag.agit.CancellationSignaller;
import com.madgag.agit.Progress;
import com.madgag.agit.ProgressListener;

import static java.lang.Thread.currentThread;

public abstract class GitOperation implements CancellationSignaller {
    
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
        return execute();
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
        cancelled=true;
        if (executionThread!=null) {
            Log.d(TAG, "Interrupting "+executionThread+" due to cancel");
            executionThread.interrupt();
        }
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
}
