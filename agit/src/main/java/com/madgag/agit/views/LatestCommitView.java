package com.madgag.agit.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.inject.Inject;
import com.madgag.agit.R;
import com.madgag.agit.RDTRemote;
import com.madgag.agit.RepoSummary;
import com.madgag.agit.RepositoryManagementActivity;

import java.util.List;

import static com.madgag.agit.R.id.latest_commit;
import static com.madgag.agit.R.layout.latest_commit_view;

public class LatestCommitView extends LinearLayout {
    @Inject
    public LatestCommitView(Context context, LayoutInflater layoutInflater, RepoSummary repoSummary) {
        super(context);
        layoutInflater.inflate(latest_commit_view, this);

        ObjectSummaryView objectSummaryView = (ObjectSummaryView) findViewById(latest_commit);
        objectSummaryView.setObject(repoSummary.getLatestCommit());
    }
}
