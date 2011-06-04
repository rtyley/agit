package com.madgag.agit;

import android.view.View;
import android.widget.AdapterView;
import com.commonsware.cwac.sacklist.SackOfViewsAdapter;
import com.google.inject.Inject;
import com.madgag.agit.views.*;

import static java.util.Arrays.asList;

public class RepoSummaryAdapter extends SackOfViewsAdapter {
    @Inject
    RepoSummaryAdapter( LatestCommitView latestCommitView,
                        RemotesSummaryView remotesSummaryView,
                        BranchesSummaryView branchesSummaryView,
                        TagsSummaryView tagsSummaryView) {
        super(asList((View)remotesSummaryView,latestCommitView, branchesSummaryView, tagsSummaryView));
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
