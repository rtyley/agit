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

import static com.madgag.agit.GitIntents.gitDirFrom;
import static com.madgag.android.ActionBarUtil.fixImageTilingOn;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.madgag.agit.guice.RepositoryScope;

import java.io.File;

import org.eclipse.jgit.lib.Repository;

import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;

public abstract class RepoScopedActivityBase extends RoboSherlockFragmentActivity {

    private
    @Inject
    @Named("gitdir")
    File gitdir;
    private
    @Inject
    RepositoryContext rc;
    private
    @Inject
    Repository repository;
    protected
    @Inject
    RepositoryScope repositoryScope;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        RepositoryScope repositoryScope = enterRepositoryScopeFor(this, getIntent());
        try {
            super.onCreate(savedInstanceState);
        } finally {
            repositoryScope.exit();
        }
        fixImageTilingOn(getSupportActionBar());
    }

    static RepositoryScope enterRepositoryScopeFor(Context context, Intent intent) {
        RepositoryScope repositoryScope = RoboGuice.getInjector(context).getInstance(RepositoryScope.class);
        repositoryScope.enterWithRepoGitdir(gitDirFrom(intent));
        return repositoryScope;
    }

    @Override
    protected void onResume() {
        super.onResume();
        rc.onResume();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return rc.onCreateDialog(id);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        rc.onPrepareDialog(id, dialog);
    }

    @Override
    protected void onPause() {
        super.onPause();
        rc.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rc.onDestroy();
    }

    protected Repository repo() {
        return repository;
    }

    protected File gitdir() {
        return gitdir;
    }


    public void onRepoScopedResume() {
    }

    public void onRepoScopedPause() {
    }

    public void onRepoScopedDestroy() {
    }
}
