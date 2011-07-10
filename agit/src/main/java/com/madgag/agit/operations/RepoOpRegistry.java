package com.madgag.agit.operations;


import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.madgag.agit.guice.RepositoryScoped;

import java.io.File;

import static com.google.common.collect.Lists.newArrayList;

@RepositoryScoped
public class RepoOpRegistry {

    private final static String TAG= "ROR";
    private @Inject @Named("gitdir") File gitdir;
    
    private GitOperation currentOperation;

    public synchronized boolean setCurrentOperation(GitOperation op, boolean interruptExistingOp) {
        if (currentOperation==null) {
            Log.d(TAG, "No current op for "+gitdir+" : setting to "+op);
            currentOperation = op;
            return true;
        }
        if (currentOperation.isDone()) {
            Log.d(TAG, "Prior finished op for  "+gitdir+" : "+currentOperation+" - new op="+op);
            currentOperation = op;
            return true;
        }
        if (interruptExistingOp) {
            Log.d(TAG, "Interrupting existing op for "+gitdir+" : "+currentOperation+" -> "+op);
            currentOperation.cancel();
            currentOperation = op;
            return true;
        }
        Log.d(TAG, "Won't interrupt existing op for "+gitdir+" : "+currentOperation);
        return false;
    }

    public synchronized GitOperation getCurrentOperation() {
        return currentOperation;
    }

    public void addRepoListener(ProgressListener<Progress> progressListener) {
    }
}
