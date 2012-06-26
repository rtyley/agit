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

import static com.madgag.agit.GitIntents.EXTRA_SOURCE_URI;
import static com.madgag.agit.GitIntents.EXTRA_TARGET_DIR;
import static com.madgag.agit.GitIntents.GITDIR;
import static com.madgag.agit.GitIntents.actionWithSuffix;
import android.content.Intent;
import android.os.Bundle;

import java.io.File;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.RemoteConfig;

public class GitIntentBuilder {


    private final Intent intent;

    public GitIntentBuilder(String actionSuffix) {
        intent = new Intent(actionWithSuffix(actionSuffix));
    }

    public GitIntentBuilder(String actionSuffix, Bundle sourceArgs, String... extrasToCopy) {
        this(actionSuffix);
//        for (String extraToCopy : extrasToCopy) { ??
//            intent.replaceExtras()
//            intent.putExtra(extraToCopy, sourceArgs.getString(extraToCopy));
//        }
        intent.putExtras(sourceArgs);
    }

    public GitIntentBuilder gitdir(File gitdir) {
        return add(GITDIR, gitdir.getAbsolutePath());
    }

    public GitIntentBuilder branch(Ref branch) {
        return branch(branch.getName());
    }

    public GitIntentBuilder branch(String branch) {
        return add("branch", branch);
    }

    public GitIntentBuilder remote(RemoteConfig remoteConfig) {
        return add("remote", remoteConfig.getName());
    }

    public GitIntentBuilder tag(String tagName) {
        return add("tag", tagName);
    }

    public GitIntentBuilder sourceUri(String sourceUri) {
        return add(EXTRA_SOURCE_URI, sourceUri);
    }

    public GitIntentBuilder targetDir(String targetDir) {
        return add(EXTRA_TARGET_DIR, targetDir);
    }

    public GitIntentBuilder add(String fieldName, String value) {
        intent.putExtra(fieldName, value);
        return this;
    }

    public GitIntentBuilder repository(Repository repository) {
        return gitdir(repository.getDirectory());
    }

    public GitIntentBuilder commit(RevCommit revCommit) {
        return commit(revCommit.name());
    }

    public GitIntentBuilder commit(String commitId) {
        return add("commit", commitId);
    }

    public GitIntentBuilder revision(String revision) {
        return add(GitIntents.REVISION, revision);
    }

    public GitIntentBuilder path(String path) {
        return add("path", path);
    }

    public Intent toIntent() {
        return intent;
    }
}
