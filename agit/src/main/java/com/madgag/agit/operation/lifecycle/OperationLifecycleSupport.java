package com.madgag.agit.operation.lifecycle;

import com.madgag.agit.Progress;
import com.madgag.agit.operations.OpNotification;

public interface OperationLifecycleSupport {
	void startedWith(OpNotification ongoingNotification);
	
	void publish(Progress progress);
	
	void completed(OpNotification completionNotification);
}
