package com.madgag.agit.operation.lifecycle;

import android.widget.RemoteViews;

import com.madgag.agit.R;
import com.madgag.agit.operations.Progress;
import com.madgag.agit.operations.ProgressListener;

public class StatusBarProgressView implements ProgressListener<Progress> {

    private final RemoteViews view;

    public StatusBarProgressView(RemoteViews view) {
        this.view = view;
    }

    public void publish(Progress... values) {
        Progress p = values[values.length - 1];
        view.setProgressBar(android.R.id.progress, p.totalWork, p.totalCompleted, p.isIndeterminate());
        view.setTextViewText(R.id.status_text, p.msg);
    }
}
