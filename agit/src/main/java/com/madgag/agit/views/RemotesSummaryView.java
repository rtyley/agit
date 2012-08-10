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

package com.madgag.agit.views;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.inject.Inject;
import com.madgag.agit.R;
import com.madgag.agit.git.model.RDTRemote;

public class RemotesSummaryView extends LinearLayout {
    private final TextView remoteSummaryTextView;
    private final RDTRemote repoRemotes;
    private static final String TAG = "RSV";

    @Inject
    public RemotesSummaryView(Context context, LayoutInflater layoutInflater, RDTRemote repoRemotes) {
        super(context);
        this.repoRemotes = repoRemotes;
        layoutInflater.inflate(R.layout.remotes_summary_view, this);
        remoteSummaryTextView = (TextView) findViewById(R.id.remote_summary_thing);
        Log.d(TAG, "remoteSummaryTextView : " + remoteSummaryTextView);
        Log.d(TAG, "getChildCount() : " + getChildCount());

        updateStuff();
    }

    private void updateStuff() {
        CharSequence text = repoRemotes.summariseAll();
        Log.d(TAG, "Remote summary : " + text);
        remoteSummaryTextView.setText(text);
    }

}
