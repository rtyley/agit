package com.madgag.agit;

import android.view.View;
import com.commonsware.cwac.sacklist.SackOfViewsAdapter;
import com.google.inject.Inject;
import com.madgag.agit.views.BranchesSummaryView;
import com.madgag.agit.views.LatestCommitView;
import com.madgag.agit.views.RemotesSummaryView;
import com.madgag.agit.views.TagsSummaryView;

import static java.util.Arrays.asList;

public class RepoSummaryAdapter extends SackOfViewsAdapter {
    @Inject
    RepoSummaryAdapter( LatestCommitView latestCommitView,
                        RemotesSummaryView remotesSummaryView,
                        BranchesSummaryView branchesSummaryView,
                        TagsSummaryView tagsSummaryView) {
        super(asList((View)latestCommitView, remotesSummaryView, branchesSummaryView, tagsSummaryView));
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}
