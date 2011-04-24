package com.madgag.agit;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.madgag.android.listviews.ViewHolder;

import java.io.File;

import static android.text.TextUtils.TruncateAt.MIDDLE;

public class RepositoryViewHolder implements ViewHolder<File> {
    private final TextView title,detail;

    public RepositoryViewHolder(View v) {
        title = (TextView) v.findViewById(android.R.id.text1);
        detail = (TextView) v.findViewById(android.R.id.text2);
        detail.setEllipsize(MIDDLE);
        detail.setSingleLine();
    }

    public void updateViewFor(File file) {
        title.setText(file.getName());
        detail.setText(file.getAbsolutePath());
    }
}
