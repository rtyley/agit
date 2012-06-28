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

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.madgag.agit.CommitNavigationView.CommitSelectedListener;
import static com.madgag.agit.GitIntents.GITDIR;
import static com.madgag.agit.GitIntents.REVISION;
import static com.madgag.agit.GitIntents.gitDirFrom;
import static com.madgag.agit.GitIntents.revisionIdFrom;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.madgag.agit.views.ObjectIdView;
import com.madgag.agit.views.PersonIdentView;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;

/**
 * Commit details is specified by: 1 Repo, 1 revision
 */
public class CommitDetailsFragment extends RoboSherlockFragment {

    private static final String TAG = "CommitDiffFragment";

    private CommitSelectedListener commitSelectedListener;

    public static CommitDetailsFragment newInstance(File gitdir, String revision) {
        CommitDetailsFragment f = new CommitDetailsFragment();

        Bundle args = new Bundle();
        args.putString(GITDIR, gitdir.getAbsolutePath());
        args.putString(REVISION, revision);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {
            Repository repository = new FileRepository(gitDirFrom(getArguments()));
            ObjectId commitId = revisionIdFrom(repository, getArguments(), REVISION);
            Log.d(TAG, "onCreateView with "+commitId);

            View v=inflater.inflate(R.layout.commit_detail_view, container, false);

            CommitNavigationView commitNavigationView = (CommitNavigationView) v.findViewById(R.id.commit_navigation);

            commitNavigationView.setCommitSelectedListener(commitSelectedListener);
            PlotCommit<PlotLane> commit = commitSelectedListener.plotCommitFor(commitId);

            commitNavigationView.setCommit(commit);

            ((ObjectIdView) v.findViewById(R.id.commit_id)).setObjectId(commit);

            ViewGroup vg = (ViewGroup) v.findViewById(R.id.commit_people_group);

            PersonIdent author = commit.getAuthorIdent(), committer = commit.getCommitterIdent();
            if (author.equals(committer)) {
                addPerson("Author & Committer", author, vg);
            } else {
                addPerson("Author", author, vg);
                addPerson("Committer", committer, vg);
            }
            TextView textView = (TextView) v.findViewById(R.id.commit_message_text);
            textView.setText(commit.getFullMessage());
            return v;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addPerson(String title, PersonIdent person, ViewGroup vg) {
        PersonIdentView personIdentView = new PersonIdentView(getActivity(), null);
        personIdentView.setIdent(title, person);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        layoutParams.weight = 1;
        vg.addView(personIdentView, layoutParams);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof CommitSelectedListener) {
            commitSelectedListener = (CommitSelectedListener) activity;
        }
    }
}
