package com.madgag.agit;

import android.view.View;
import android.widget.TextView;
import com.madgag.android.listviews.ViewHolder;

public class SuggestedRepoViewHolder implements ViewHolder<SuggestedRepo> {
    private final TextView title,detail;

    public SuggestedRepoViewHolder(View v) {
        title = (TextView) v.findViewById(android.R.id.text1);
        detail = (TextView) v.findViewById(android.R.id.text2);
    }

    public void updateViewFor(SuggestedRepo repo) {
        title.setText(repo.getName());
        detail.setText(repo.getURI());
    }
}

