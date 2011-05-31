package com.madgag.agit;

import android.view.View;
import android.widget.TextView;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.madgag.android.listviews.ViewHolder;
import org.eclipse.jgit.revwalk.RevCommit;

import static com.madgag.agit.R.id.branch_name;

public class BranchViewHolder implements ViewHolder<RDTBranch.BranchSummary> {
    private final TextView branchName;
    private final ViewHolder<RevCommit> commitViewHolder;

    @Inject
    public BranchViewHolder(@Assisted View v, CommitViewHolderFactory commitViewHolderFactory) {
        commitViewHolder = commitViewHolderFactory.createViewHolderFor(v);
        branchName = (TextView) v.findViewById(branch_name);
    }

    public void updateViewFor(RDTBranch.BranchSummary branch) {
        branchName.setText(branch.getShortName());
        commitViewHolder.updateViewFor(branch.getLatestCommit());
    }
}
