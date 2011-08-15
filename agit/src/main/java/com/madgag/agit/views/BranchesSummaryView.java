package com.madgag.agit.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.inject.Inject;
import com.madgag.agit.*;
import com.madgag.agit.git.model.RDTBranch;
import org.eclipse.jgit.lib.Repository;

import static com.madgag.agit.R.drawable.branch_icon;
import static com.madgag.agit.R.layout.simple_summary_list_item;
import static com.madgag.agit.RDTypeListActivity.listIntent;

public class BranchesSummaryView extends RelativeLayout implements EnabledListItem {

    private final TextView detail, title;
    private final Repository repository;
    private final RDTBranch repoBranches;

    @Inject
    public BranchesSummaryView(Context context, LayoutInflater layoutInflater, Repository repository, RDTBranch repoBranches) {
        super(context);
        this.repository = repository;
        this.repoBranches = repoBranches;
        layoutInflater.inflate(simple_summary_list_item, this);
         ((ImageView) findViewById(R.id.rdt_icon)).setImageResource(branch_icon);
        title = (TextView) findViewById(R.id.title);
        detail = (TextView) findViewById(R.id.detail);

        updateStuff();
    }

    private void updateStuff() {
        title.setText(repoBranches.conciseSummaryTitle());
        detail.setText(repoBranches.summariseAll());
    }

    public void onItemClick() {
        getContext().startActivity(listIntent(repository, repoBranches.name()));
    }
}
