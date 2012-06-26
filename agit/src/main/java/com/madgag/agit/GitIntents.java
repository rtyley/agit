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

import static com.madgag.agit.git.Repos.openRepoFor;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public class GitIntents {

    private static final String TAG = "GitIntents";

    public static final String OPEN_GIT_INTENT_PREFIX = "org.openintents.git.";

    public static final String REPO_STATE_CHANGED_BROADCAST = "repo.UPDATED";

    public static String actionWithSuffix(String actionSuffix) {
        return OPEN_GIT_INTENT_PREFIX + actionSuffix;
    }


    public static Intent broadcastIntentForRepoStateChange(File gitdir) {
        return new GitIntentBuilder(REPO_STATE_CHANGED_BROADCAST).gitdir(gitdir).toIntent();
    }

    public static final String
            BARE = "bare",
            EXTRA_TARGET_DIR = "target-dir",
            EXTRA_SOURCE_URI = "source-uri",
            GITDIR = "gitdir",
            UNTIL_REVS = "until-revs",
            BEFORE_REV = "before-rev",
            AFTER_REV = "after-rev",
            REVISION = "revision",
            COMMIT = "commit",
            PATH = "path";

    public static File directoryFrom(Intent intent) {
        String directory = intent.getStringExtra("directory");
        return new File(directory);
    }

    public static String sourceUriFrom(Intent intent) {
        return intent.getStringExtra(EXTRA_SOURCE_URI);
    }

    public static File gitDirFrom(Intent intent) {
        return gitDirFrom(intent.getExtras());
    }

    public static File gitDirFrom(Bundle extras) {
        String gitdirString = extras.getString(GITDIR);
        Log.i(TAG, "gitdirString = " + gitdirString);
        File gitdir = new File(gitdirString);
        Log.i(TAG, "gitdir for = " + gitdir.getAbsolutePath());
        return gitdir;
    }

    public static RevCommit commitFrom(Repository repository, Bundle args, String revisionArgName) throws IOException {
        return new RevWalk(repository).parseCommit(revisionIdFrom(repository, args, revisionArgName));
    }

    public static ObjectId revisionIdFrom(Repository repo, Bundle args, String revisionArgName) throws IOException {
        return repo.resolve(args.getString(revisionArgName));
    }

    public static String branchNameFrom(Intent intent) {
        return intent.getStringExtra("branch");
    }

    public static String tagNameFrom(Intent intent) {
        return intent.getStringExtra("tag");
    }

    public static void addDirectoryTo(Intent intent, File directory) {
        intent.putExtra("directory", directory.getAbsolutePath());
    }

    public static void addGitDirTo(Intent intent, File gitdir) {
        intent.putExtra(GITDIR, gitdir.getAbsolutePath());
    }

    public static Repository repositoryFrom(Intent intent) {
        return openRepoFor(gitDirFrom(intent));
    }

    public static ObjectId commitIdFrom(Intent intent) {
        return ObjectId.fromString(intent.getStringExtra(COMMIT));
    }


}
