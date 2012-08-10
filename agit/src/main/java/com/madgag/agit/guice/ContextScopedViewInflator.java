/*
 * Copyright (c) 2011, 2012 Roberto Tyley
 *
 * This file is part of 'Agit' - an Android Git client.
 *
 * Agit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Agit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

package com.madgag.agit.guice;

import static android.view.LayoutInflater.from;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.madgag.android.listviews.ViewCreator;

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
