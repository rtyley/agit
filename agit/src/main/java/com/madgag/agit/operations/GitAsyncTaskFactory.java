package com.madgag.agit.operations;

import com.madgag.agit.operation.lifecycle.OperationLifecycleSupport;

public interface GitAsyncTaskFactory {
    public GitAsyncTask createTaskFor(GitOperation operation, OperationLifecycleSupport lifecycleSupport);
}
