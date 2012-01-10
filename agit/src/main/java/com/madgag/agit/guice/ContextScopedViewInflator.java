package com.madgag.agit.guice;

import static android.view.LayoutInflater.from;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.madgag.android.listviews.ViewCreator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import roboguice.inject.ContextScope;
import roboguice.inject.ContextSingleton;

@ContextSingleton
public class ContextScopedViewInflator implements ViewCreator {

    private final ContextScope scope;
    private final Context context;
    private final LayoutInflater inflater;
    private final int resId;

    @Inject
    public ContextScopedViewInflator(ContextScope scope, @Assisted Context context, @Assisted int resId) {
        this.context = context;
        this.scope = scope;
        this.resId = resId;
        inflater = from(context);
    }

    @Override
    public View createBlankView() {
        synchronized (ContextScope.class) {
            scope.enter(context);
            try {
                return inflater.inflate(resId, null);
            } finally {
                scope.exit(context);
            }
        }
    }
}
