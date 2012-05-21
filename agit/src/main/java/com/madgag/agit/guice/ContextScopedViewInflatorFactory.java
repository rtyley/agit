package com.madgag.agit.guice;

import android.content.Context;

import com.madgag.android.listviews.ViewCreator;


public interface ContextScopedViewInflatorFactory {
    public ViewCreator creatorFor(Context context, int resId);
}
