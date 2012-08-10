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

import static com.madgag.agit.R.id.commit_subject;
import static com.madgag.agit.R.id.commit_time;
import static com.madgag.agit.R.id.repo_name;
import static com.madgag.agit.git.Repos.niceNameFor;
import static com.madgag.agit.views.TextUtil.ITALIC_CLIPPING_BUFFER;
import android.view.View;
import android.widget.TextView;

import com.madgag.agit.util.Time;
import com.madgag.android.listviews.ViewHolder;

import org.eclipse.jgit.revwalk.RevCommit;

public class RepositoryViewHolder implements ViewHolder<RepoSummary> {
    private final TextView title, detail, commitTime;

    public RepositoryViewHolder(View v) {
        title = (TextView) v.findViewById(repo_name);
        detail = (TextView) v.findViewById(commit_subject);
        commitTime = (TextView) v.findViewById(commit_time);
    }

    public void updateViewFor(RepoSummary repoSummary) {
        title.setText(niceNameFor(repoSummary.getRepo()));
        CharSequence commitTimeText = "...";
        RevCommit latestCommit = repoSummary.getLatestCommit();
        if (latestCommit != null) {
            detail.setText(repoSummary.getLatestCommit().getShortMessage());
            commitTimeText = Time.timeSinceSeconds(latestCommit.getCommitTime());
        } else {
            detail.setText(repoSummary.getRepo().getDirectory().getAbsolutePath());
        }
        commitTime.setText(commitTimeText + ITALIC_CLIPPING_BUFFER);
    }
}
