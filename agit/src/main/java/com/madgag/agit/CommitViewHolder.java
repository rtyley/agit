package com.madgag.agit;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.madgag.android.lazydrawables.ImageSession;
import com.madgag.android.listviews.ViewHolder;
import org.eclipse.jgit.revwalk.RevCommit;

import static com.madgag.android.lazydrawables.gravatar.Gravatars.gravatarIdFor;

public class CommitViewHolder implements ViewHolder<RevCommit> {
    private final TextView commit_shortdesc,commit_date;
    private final ImageView gravatar;
    private final ImageSession avatarSession;

    public CommitViewHolder(View v, ImageSession avatarSession) {
        this.avatarSession = avatarSession;
        commit_date = (TextView) v.findViewById(R.id.tv_commit_list_item_commit_date);
        commit_shortdesc = (TextView) v.findViewById(R.id.tv_commit_list_item_shortdesc);
        gravatar = (ImageView) v.findViewById(R.id.iv_commit_list_item_gravatar);
    }

    public void updateViewFor(RevCommit commit) {
        commit_date.setText(Time.timeSinceSeconds(commit.getCommitTime()));

        Drawable avatarBitmap = avatarSession.get(gravatarIdFor(commit.getAuthorIdent().getEmailAddress()));
        gravatar.setImageDrawable(avatarBitmap);

        commit_shortdesc.setText(commit.getShortMessage());
    }
}
