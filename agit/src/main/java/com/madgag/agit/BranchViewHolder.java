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
