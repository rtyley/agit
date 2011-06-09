package com.madgag.agit.operation.lifecycle;

import com.madgag.agit.Progress;
import com.madgag.agit.operations.OpNotification;

public class CasualShortTermLifetime implements OperationLifecycleSupport {

	public void startedWith(OpNotification startNotification) {
		// start some kind of animation in the RMA
	}

	public void publish(Progress progress) {
		// TODO Auto-generated method stub
		
	}

    public void error(OpNotification completionNotification) {}
    public void success(OpNotification completionNotification) {}

	public void completed(OpNotification completionNotification) {
		// stop the animation, I guess
	}


}
