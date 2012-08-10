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

import static com.madgag.agit.GitIntents.GITDIR;
import static com.madgag.agit.GitIntents.UNTIL_REVS;
import static com.madgag.agit.RDTypeListActivity.listIntent;
import static com.madgag.agit.git.Repos.niceNameFor;
import static com.madgag.android.ActionBarUtil.fixImageTilingOn;
import static com.madgag.android.ActionBarUtil.homewardsWith;
import static com.madgag.android.ActionBarUtil.setPrefixedTitleOn;
import static java.util.Arrays.asList;
import static org.eclipse.jgit.lib.Repository.shortenRefName;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.inject.Inject;
import com.madgag.agit.operations.GitAsyncTaskFactory;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;

import roboguice.inject.InjectExtra;

public class BranchViewer extends RepoScopedActivityBase {


    public static GitIntentBuilder commitViewIntentFor(Bundle sourceArgs) {
        return new GitIntentBuilder("commit.VIEW", sourceArgs, GITDIR, UNTIL_REVS);
    }

    public static Intent branchViewerIntentFor(File gitdir, String branch) {
        return new GitIntentBuilder("branch.VIEW").gitdir(gitdir).branch(branch).toIntent();
    }

    private final static int CHECKOUT_ID = Menu.FIRST;

    private static final String TAG = "BranchViewer";

    @Inject
    GitAsyncTaskFactory gitAsyncTaskFactory;

    @InjectExtra(value = "branch")
    String branchName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fixImageTilingOn(getSupportActionBar());

        ActionBar actionBar = getSupportActionBar();
        setPrefixedTitleOn(actionBar, niceNameFor(repo()), shortenRefName(branch().getName()));
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            LogFragment f = LogFragment.newInstance(gitdir(), asList(branch().getName()), null);
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, f).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // menu.add(0, CHECKOUT_ID, 0, checkout_commit_menu_option).setShortcut('0', 'c');
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return homewardsWith(this, listIntent(repo(), "branch"));
            case CHECKOUT_ID:
                try {
                    new Git(repo()).checkout().setName(branchName).call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Ref branch() {
        try {
            return repo().getRef(branchName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
