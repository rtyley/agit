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
