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

import static com.madgag.agit.GitIntents.AFTER_REV;
import static com.madgag.agit.GitIntents.BEFORE_REV;
import static com.madgag.agit.GitIntents.GITDIR;
import static com.madgag.agit.GitIntents.commitFrom;
import static com.madgag.agit.GitIntents.gitDirFrom;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.madgag.agit.diff.CommitChangeListAdapter;
import com.madgag.agit.diff.DiffSliderView;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * Commit diff is specified by: 1 Repo, 1 before-rev, 1 after-rev
 */
public class CommitDiffFragment extends RoboSherlockFragment {

    private static final String TAG = "CommitDiffFragment";

    public static CommitDiffFragment newInstance(File gitdir, String before, String after) {
        CommitDiffFragment f = new CommitDiffFragment();

        Bundle args = new Bundle();
        args.putString(GITDIR, gitdir.getAbsolutePath());
        args.putString(BEFORE_REV, before);
        args.putString(AFTER_REV, after);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            Repository repository = FileRepositoryBuilder.create(gitDirFrom(getArguments()));
            RevCommit before = commitFrom(repository, getArguments(), BEFORE_REV), after = commitFrom(repository, getArguments(), AFTER_REV);
            Log.d(TAG, "onCreateView before = " + before);
            Log.d(TAG, "onCreateView after  = " + after);

            View v = inflater.inflate(R.layout.rev_commit_view, container, false);
            DiffSliderView diffSlider = (DiffSliderView) v.findViewById(R.id.RevCommitDiffSlider);
            ExpandableListView expandableList = (ExpandableListView) v.findViewById(android.R.id.list);
            expandableList.setAdapter(new CommitChangeListAdapter(repository, after, before, diffSlider,
                    expandableList, getActivity()));
            return v;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
