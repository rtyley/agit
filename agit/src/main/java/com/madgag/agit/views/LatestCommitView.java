package com.madgag.agit.views;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import com.google.inject.Inject;
import com.madgag.agit.*;
import org.eclipse.jgit.revwalk.RevCommit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.madgag.agit.CommitViewerActivity.revCommitViewIntentFor;
import static com.madgag.agit.R.id.latest_commit;
import static com.madgag.agit.R.layout.latest_commit_view;

public class LatestCommitView extends FrameLayout implements EnabledListItem {
    private final RepoSummary repoSummary;

    @Inject
    public LatestCommitView(Context context, LayoutInflater layoutInflater, RepoSummary repoSummary) {
        super(context);
        this.repoSummary = repoSummary;
        layoutInflater.inflate(latest_commit_view, this);

        PrettyCommitSummaryView objectSummaryView = (PrettyCommitSummaryView) findViewById(latest_commit);
        RevCommit latestCommit = repoSummary.getLatestCommit();
        if (latestCommit ==null) {
            objectSummaryView.setVisibility(GONE);
        } else {
            objectSummaryView.setCommit(latestCommit);
            objectSummaryView.setVisibility(VISIBLE);
        }
        objectSummaryView.setBackgroundResource(R.drawable.single_line_frame);
    }

    public void onItemClick() {
        if (repoSummary.hasCommits()) {
            getContext().startActivity(revCommitViewIntentFor(repoSummary, repoSummary.getLatestCommit()));
        }
    }

}
