package com.madgag.agit;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.madgag.agit.views.TextUtil;
import com.madgag.android.lazydrawables.ImageSession;
import com.madgag.android.listviews.ViewHolder;
import org.eclipse.jgit.revwalk.RevCommit;

import static com.madgag.agit.R.id.iv_commit_list_item_gravatar;
import static com.madgag.agit.R.id.tv_commit_list_item_commit_date;
import static com.madgag.agit.R.id.tv_commit_list_item_shortdesc;
import static com.madgag.agit.views.TextUtil.ITALIC_CLIPPING_BUFFER;
import static com.madgag.android.lazydrawables.gravatar.Gravatars.gravatarIdFor;

public class CommitViewHolder implements ViewHolder<RevCommit> {
    private final TextView commit_shortdesc,commit_date;
    private final ImageView gravatar;
    private final ImageSession avatarSession;

    @Inject
    public CommitViewHolder(@Assisted View v, ImageSession avatarSession) {
        this.avatarSession = avatarSession;
        commit_date = (TextView) v.findViewById(tv_commit_list_item_commit_date);
        commit_shortdesc = (TextView) v.findViewById(tv_commit_list_item_shortdesc);
        gravatar = (ImageView) v.findViewById(iv_commit_list_item_gravatar);
    }

    public void updateViewFor(RevCommit commit) {
        commit_date.setText(Time.timeSinceSeconds(commit.getCommitTime())+ ITALIC_CLIPPING_BUFFER);

        Drawable avatarBitmap = avatarSession.get(gravatarIdFor(commit.getAuthorIdent().getEmailAddress()));
        gravatar.setImageDrawable(avatarBitmap);

        commit_shortdesc.setText(commit.getShortMessage());
    }
}
