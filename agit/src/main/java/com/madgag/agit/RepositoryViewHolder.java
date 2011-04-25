package com.madgag.agit;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.madgag.android.listviews.ViewHolder;

import java.io.File;

import static android.text.TextUtils.TruncateAt.MIDDLE;
import static com.madgag.agit.Repos.niceNameFor;

public class RepositoryViewHolder implements ViewHolder<File> {
    private final TextView title,detail;

    public RepositoryViewHolder(View v) {
        title = (TextView) v.findViewById(android.R.id.text1);
        detail = (TextView) v.findViewById(android.R.id.text2);
        detail.setEllipsize(MIDDLE);
        detail.setSingleLine();
    }

    public void updateViewFor(File gitdir) {
        title.setText(niceNameFor(gitdir));
        detail.setText(gitdir.getAbsolutePath());
    }
}
