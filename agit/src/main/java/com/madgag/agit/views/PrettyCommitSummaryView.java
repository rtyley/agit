/*
 * Copyright (c) 2011 Roberto Tyley
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.madgag.agit.views;

import static com.madgag.agit.R.id.iv_commit_list_item_gravatar;
import static com.madgag.agit.R.id.tv_commit_list_item_commit_date;
import static com.madgag.agit.R.id.tv_commit_list_item_shortdesc;
import static com.madgag.agit.R.layout.commit_summary_view;
import static com.madgag.agit.views.TextUtil.ITALIC_CLIPPING_BUFFER;
import static com.madgag.agit.views.ViewUtil.injectFor;
import static com.madgag.android.lazydrawables.gravatar.Gravatars.gravatarIdFor;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.madgag.agit.util.Time;
import com.madgag.android.lazydrawables.ImageSession;

import org.eclipse.jgit.revwalk.RevCommit;

public class PrettyCommitSummaryView extends FrameLayout {

    private static final String TAG = "PCSV";

    private final TextView shortMessage, commit_date;
    private final ImageView gravatar;
    @Inject
    ImageSession avatarSession;

    public PrettyCommitSummaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        injectFor(this, commit_summary_view);

        shortMessage = (TextView) findViewById(tv_commit_list_item_shortdesc);
        commit_date = (TextView) findViewById(tv_commit_list_item_commit_date);
        gravatar = (ImageView) findViewById(iv_commit_list_item_gravatar);
    }

    public void setCommit(RevCommit commit) {
        commit_date.setText(Time.timeSinceSeconds(commit.getCommitTime()) + ITALIC_CLIPPING_BUFFER);

        Drawable avatar = avatarSession.get(gravatarIdFor(commit.getAuthorIdent().getEmailAddress()));
        gravatar.setImageDrawable(avatar);

        shortMessage.setText(commit.getShortMessage() + ITALIC_CLIPPING_BUFFER);
    }
}
