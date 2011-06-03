package com.madgag.agit.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.inject.Inject;
import com.madgag.agit.R;
import com.madgag.agit.RDTTag;

import static android.R.layout.simple_list_item_2;
import static com.madgag.agit.R.layout.simple_summary_list_item;

public class TagsSummaryView extends RelativeLayout {
    private final TextView detail, title;
    private final RDTTag repoTags;

    @Inject
    public TagsSummaryView(Context context, LayoutInflater layoutInflater, RDTTag repoTags) {
        super(context);
        this.repoTags = repoTags;
        layoutInflater.inflate(simple_summary_list_item, this);
        title = (TextView) findViewById(R.id.title);
        detail = (TextView) findViewById(R.id.detail);

        updateStuff();
    }

    private void updateStuff() {
        title.setText(repoTags.conciseSummaryTitle());
        detail.setText(repoTags.summariseAll());
    }

}
