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

package com.madgag.agit;

import static java.util.Arrays.asList;
import android.view.View;
import android.widget.AdapterView;

import com.commonsware.cwac.sacklist.SackOfViewsAdapter;
import com.google.inject.Inject;
import com.madgag.agit.views.BranchesSummaryView;
import com.madgag.agit.views.EnabledListItem;
import com.madgag.agit.views.LatestCommitView;
import com.madgag.agit.views.RemotesSummaryView;
import com.madgag.agit.views.TagsSummaryView;

public class RepoSummaryAdapter extends SackOfViewsAdapter {
    @Inject
    RepoSummaryAdapter(LatestCommitView latestCommitView,
                       RemotesSummaryView remotesSummaryView,
                       BranchesSummaryView branchesSummaryView,
                       TagsSummaryView tagsSummaryView) {
        super(asList((View) remotesSummaryView, latestCommitView, branchesSummaryView, tagsSummaryView));
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position) instanceof EnabledListItem;
    }


    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                EnabledListItem item = (EnabledListItem) getItem(position);
                item.onItemClick();
            }
        };
    }
}
