package com.madgag.agit;

import com.madgag.agit.operation.lifecycle.OperationLifecycleSupport;
import com.madgag.agit.operations.GitAsyncTask;
import com.madgag.agit.operations.GitOperation;

public interface GitAsyncTaskFactory {
	public GitAsyncTask createTaskFor(GitOperation operation, OperationLifecycleSupport lifecycleSupport);
}
