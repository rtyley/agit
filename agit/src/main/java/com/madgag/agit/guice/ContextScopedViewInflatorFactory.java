package com.madgag.agit.guice;

import static android.view.LayoutInflater.from;

import com.google.inject.assistedinject.Assisted;
import com.madgag.agit.operation.lifecycle.OperationLifecycleSupport;
import com.madgag.agit.operations.GitAsyncTask;
import com.madgag.agit.operations.GitOperation;
import com.madgag.android.listviews.ViewCreator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import roboguice.inject.ContextScope;


public interface ContextScopedViewInflatorFactory {
	public ViewCreator creatorFor(Context context, int resId);
}
