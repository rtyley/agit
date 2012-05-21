package com.madgag.agit.operation.lifecycle;

import com.madgag.agit.operations.OpNotification;
import com.madgag.agit.operations.Progress;

public interface OperationLifecycleSupport {
    void startedWith(OpNotification ongoingNotification);

    void publish(Progress progress);

    void error(OpNotification errorNotification);

    void success(OpNotification successNotification);

    void completed(OpNotification completionNotification);
}
