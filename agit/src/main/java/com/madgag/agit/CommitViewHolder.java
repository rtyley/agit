/*
 * Copyright (c) 2011, 2012 Roberto Tyley
 *
 * This file is part of 'Agit' - an Android Git client.
 *
 * Agit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Agit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

package com.madgag.agit;

import static com.madgag.agit.R.id.iv_commit_list_item_gravatar;
import static com.madgag.agit.R.id.tv_commit_list_item_commit_date;
import static com.madgag.agit.R.id.tv_commit_list_item_shortdesc;
import static com.madgag.agit.views.TextUtil.ITALIC_CLIPPING_BUFFER;
import static com.madgag.android.lazydrawables.gravatar.Gravatars.gravatarIdFor;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.madgag.agit.util.Time;
import com.madgag.android.lazydrawables.ImageSession;
import com.madgag.android.listviews.ViewHolder;

import org.eclipse.jgit.revwalk.RevCommit;

public class CommitViewHolder implements ViewHolder<RevCommit> {
    private final TextView commit_shortdesc, commit_date;
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
        commit_date.setText(Time.timeSinceSeconds(commit.getCommitTime()) + ITALIC_CLIPPING_BUFFER);

        Drawable avatarBitmap = avatarSession.get(gravatarIdFor(commit.getAuthorIdent().getEmailAddress()));
        gravatar.setImageDrawable(avatarBitmap);

        commit_shortdesc.setText(commit.getShortMessage());
    }
}
