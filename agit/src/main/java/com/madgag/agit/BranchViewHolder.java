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

import static com.madgag.agit.R.id.branch_name;
import static com.madgag.agit.R.id.latest_commit;
import android.view.View;
import android.widget.TextView;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.madgag.agit.git.model.RDTBranch;
import com.madgag.agit.views.PrettyCommitSummaryView;
import com.madgag.android.listviews.ViewHolder;

public class BranchViewHolder implements ViewHolder<RDTBranch.BranchSummary> {
    private final TextView branchName;
    private final PrettyCommitSummaryView latestCommitView;

    @Inject
    public BranchViewHolder(@Assisted View v) {
        branchName = (TextView) v.findViewById(branch_name);
        latestCommitView = (PrettyCommitSummaryView) v.findViewById(latest_commit);
    }

    public void updateViewFor(RDTBranch.BranchSummary branch) {
        branchName.setText(branch.getShortName());
        latestCommitView.setCommit(branch.getLatestCommit());
    }
}
