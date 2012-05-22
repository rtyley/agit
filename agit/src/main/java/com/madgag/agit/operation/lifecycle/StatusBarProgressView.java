package com.madgag.agit.operation.lifecycle;

import android.widget.RemoteViews;

import com.madgag.agit.R;
import com.madgag.agit.operations.Progress;
import com.madgag.agit.operations.ProgressListener;

public class StatusBarProgressView implements ProgressListener<Progress> {

    private final RemoteViews view;
    private final int statusTextId;

    public StatusBarProgressView(RemoteViews view, int statusTextId) {
        this.view = view;
        this.statusTextId = statusTextId;
    }

    public void publish(Progress... values) {
        Progress p = values[values.length - 1];
        view.setProgressBar(android.R.id.progress, p.totalWork, p.totalCompleted, p.isIndeterminate());
        view.setTextViewText(statusTextId, p.msg);
    }
}
