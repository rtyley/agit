package com.madgag.agit.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.inject.Inject;
import com.madgag.agit.R;
import com.madgag.agit.RDTBranch;

import static android.R.layout.simple_list_item_2;
import static com.madgag.agit.R.layout.simple_summary_list_item;

public class BranchesSummaryView extends RelativeLayout {

    private final TextView detail, title;
    private final RDTBranch repoBranches;

    @Inject
    public BranchesSummaryView(Context context, LayoutInflater layoutInflater, RDTBranch repoBranches) {
        super(context);
        this.repoBranches = repoBranches;
        layoutInflater.inflate(simple_summary_list_item, this);
        title = (TextView) findViewById(R.id.title);
        detail = (TextView) findViewById(R.id.detail);

        updateStuff();
    }

    private void updateStuff() {
        title.setText(repoBranches.conciseSummaryTitle());
        detail.setText(repoBranches.summariseAll());
    }
}
