package com.madgag.agit.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.inject.Inject;
import com.madgag.agit.R;
import com.madgag.agit.RDTTag;
import org.eclipse.jgit.lib.Repository;

import static android.R.layout.simple_list_item_2;
import static com.madgag.agit.R.drawable.tag_icon;
import static com.madgag.agit.R.layout.simple_summary_list_item;
import static com.madgag.agit.RDTypeListActivity.listIntent;

public class TagsSummaryView extends RelativeLayout implements EnabledListItem {
    private final TextView detail, title;
    private final Repository repository;
    private final RDTTag repoTags;

    @Inject
    public TagsSummaryView(Context context, LayoutInflater layoutInflater, Repository repository, RDTTag repoTags) {
        super(context);
        this.repository = repository;
        this.repoTags = repoTags;
        layoutInflater.inflate(simple_summary_list_item, this);

        ((ImageView) findViewById(R.id.rdt_icon)).setImageResource(tag_icon);
        title = (TextView) findViewById(R.id.title);
        detail = (TextView) findViewById(R.id.detail);

        updateStuff();
    }

    private void updateStuff() {
        title.setText(repoTags.conciseSummaryTitle());
        detail.setText(repoTags.summariseAll());
    }

    public void onItemClick() {
        getContext().startActivity(listIntent(repository, repoTags.name()));
    }


}
