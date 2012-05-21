package com.madgag.agit;

import android.view.View;
import android.widget.TextView;

import com.madgag.agit.git.model.RepoDomainType;
import com.madgag.android.listviews.ViewHolder;

public class RDTypeInstanceViewHolder<B> implements ViewHolder<B> {
    private final TextView title, detail;
    private final RepoDomainType<B> rdt;

    public RDTypeInstanceViewHolder(RepoDomainType<B> rdt, View v) {
        this.rdt = rdt;
        detail = (TextView) v.findViewById(android.R.id.text1);
        title = (TextView) v.findViewById(android.R.id.text2);
    }

    public void updateViewFor(B e) {
        detail.setText(rdt.idFor(e));
        title.setText(rdt.shortDescriptionOf(e));
    }
}
