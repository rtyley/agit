package com.madgag.agit;

import android.view.View;
import android.widget.TextView;
import com.madgag.agit.git.model.RepoDomainType;
import com.madgag.android.listviews.ViewHolder;

public class RDTypeViewHolder implements ViewHolder<RepoDomainType<?>> {
    private final TextView detail, title;

    public RDTypeViewHolder(View v) {
        title = (TextView) v.findViewById(android.R.id.text1);
        detail = (TextView) v.findViewById(android.R.id.text2);
    }

    public void updateViewFor(RepoDomainType<?> repoDomainType) {
        title.setText(repoDomainType.conciseSummaryTitle());
        detail.setText(repoDomainType.summariseAll());
    }
}
