package com.madgag.agit;

import android.view.View;
import android.widget.TextView;
import com.madgag.android.listviews.ViewHolder;

public class RDTypeViewHolder implements ViewHolder<RepoDomainType<?>> {
    private final TextView title,detail;

    public RDTypeViewHolder(View v) {
        detail = (TextView) v.findViewById(android.R.id.text1);
        title = (TextView) v.findViewById(android.R.id.text2);
    }

    public void updateViewFor(RepoDomainType<?> repoDomainType) {
        detail.setText(repoDomainType.conciseSummaryTitle());
        title.setText(repoDomainType.summariseAll());
    }
}
