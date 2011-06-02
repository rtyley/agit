package com.madgag.agit.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.inject.Inject;
import com.madgag.agit.RDTBranch;

import static android.R.layout.simple_list_item_2;

public class BranchesSummaryView extends LinearLayout {

    private final TextView detail, title;
    private final RDTBranch repoBranches;

    @Inject
    public BranchesSummaryView(Context context, LayoutInflater layoutInflater, RDTBranch repoBranches) {
        super(context);
        this.repoBranches = repoBranches;
        layoutInflater.inflate(simple_list_item_2, this);
        title = (TextView) findViewById(android.R.id.text1);
        detail = (TextView) findViewById(android.R.id.text2);

        updateStuff();
    }

    private void updateStuff() {
        title.setText(repoBranches.conciseSummaryTitle());
        detail.setText(repoBranches.summariseAll());
    }
}
