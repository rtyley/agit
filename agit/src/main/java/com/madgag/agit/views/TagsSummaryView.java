package com.madgag.agit.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.inject.Inject;
import com.madgag.agit.RDTTag;

import static android.R.layout.simple_list_item_2;

public class TagsSummaryView extends LinearLayout {
    private final TextView detail, title;
    private final RDTTag repoTags;

    @Inject
    public TagsSummaryView(Context context, LayoutInflater layoutInflater, RDTTag repoTags) {
        super(context);
        this.repoTags = repoTags;
        layoutInflater.inflate(simple_list_item_2, this);
        title = (TextView) findViewById(android.R.id.text1);
        detail = (TextView) findViewById(android.R.id.text2);

        updateStuff();
    }

    private void updateStuff() {
        title.setText(repoTags.conciseSummaryTitle());
        detail.setText(repoTags.summariseAll());
    }

}
