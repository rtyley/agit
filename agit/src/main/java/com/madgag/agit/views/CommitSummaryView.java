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

import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;
import com.madgag.agit.CommitViewerActivity;
import com.madgag.agit.R;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import static com.madgag.agit.CommitViewerActivity.revCommitViewIntentFor;
import static com.madgag.agit.R.drawable.commit_36;
import static com.madgag.agit.R.drawable.commit_icon;

public class CommitSummaryView extends OSV<RevCommit> {

	public void setObject(final RevCommit commit, View view, final Repository repo) {
        ((PersonIdentView) view.findViewById(R.id.author_ident)).setIdent("Author", commit.getAuthorIdent());
        ((TextView) view.findViewById(R.id.short_message_text)).setText(commit.getShortMessage());
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                view.getContext().startActivity(revCommitViewIntentFor(repo.getDirectory(), commit.name()));
            }
        });
    }

    @Override
    public int iconId() {
        return commit_icon;
    }

    @Override
    public int layoutId() {
        return R.layout.osv_commit_summary_view;
    }

    @Override
    public CharSequence getTypeName() {
        return "Commit";
    }
}
