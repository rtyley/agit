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

package com.madgag.agit.diff;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import android.util.Log;

import com.google.common.base.Function;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

public class CommitDiffer {

    private static final String TAG = "CD";

    public List<FileDiff> calculateCommitDiffs(Repository repo, RevCommit parentCommit,
                                               RevCommit commit) throws IOException {
        Log.d(TAG, "calculateCommitDiffs");
        RevWalk revWalk = new RevWalk(repo);
        final TreeWalk tw = new TreeWalk(revWalk.getObjectReader());
        tw.setRecursive(true);
        tw.reset();
        tw.addTree(revWalk.parseTree(parentCommit.getTree()));
        tw.addTree(revWalk.parseTree(commit.getTree()));
        tw.setFilter(TreeFilter.ANY_DIFF);
        List<DiffEntry> files = detectRenames(repo, DiffEntry.scan(tw));

        final LineContextDiffer lineContextDiffer = new LineContextDiffer(revWalk.getObjectReader());
        return newArrayList(transform(files, new Function<DiffEntry, FileDiff>() { // transform IS JUST A VIEW
            public FileDiff apply(DiffEntry d) {
                return new FileDiff(lineContextDiffer, d);
            }
        }));
    }


    private List<DiffEntry> detectRenames(Repository repo, List<DiffEntry> files) throws IOException {
        RenameDetector rd = new RenameDetector(repo);
        rd.setRenameLimit(200); // 200^2 ain't so bad... ok, yep, totally arbitrary
        rd.addAll(files);
        return rd.compute();
    }
}
