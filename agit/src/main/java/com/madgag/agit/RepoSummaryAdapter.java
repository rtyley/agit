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
