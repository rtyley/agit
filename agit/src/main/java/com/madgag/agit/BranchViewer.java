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

import static com.madgag.agit.RDTypeListActivity.listIntent;
import static com.madgag.agit.git.Repos.niceNameFor;
import static com.madgag.android.ActionBarUtil.fixImageTilingOn;
import static com.madgag.android.ActionBarUtil.homewardsWith;
import static com.madgag.android.ActionBarUtil.setPrefixedTitleOn;
import static com.madgag.android.ViewPagerUtil.onSearchRequestedForCurrentFragment;
import static java.util.Arrays.asList;
import static org.eclipse.jgit.lib.Repository.shortenRefName;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.inject.Inject;
import com.madgag.agit.operations.GitAsyncTaskFactory;
import com.viewpagerindicator.TabPageIndicator;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;

import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class BranchViewer extends RepoScopedActivityBase {

    private static final String TAG = "BranchViewer";

    public static Intent branchViewerIntentFor(File gitdir, String branch) {
        return new GitIntentBuilder("branch.VIEW").gitdir(gitdir).branch(branch).toIntent();
    }

    private final static int CHECKOUT_ID = Menu.FIRST;

    @Inject
    GitAsyncTaskFactory gitAsyncTaskFactory;

    @InjectExtra(value = "branch")
    String branchName;

    @InjectView(R.id.pager)
    ViewPager pager;

    @InjectView(R.id.indicator)
    TabPageIndicator tabPageIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fixImageTilingOn(getSupportActionBar());
        setContentView(R.layout.branch_view);

        ActionBar actionBar = getSupportActionBar();
        setPrefixedTitleOn(actionBar, niceNameFor(repo()), shortenRefName(branch().getName()));
        actionBar.setDisplayHomeAsUpEnabled(true);

        pager.setAdapter(new BranchPagerAdapter(getSupportFragmentManager(), getResources(), gitdir(), branch()));
        tabPageIndicator.setViewPager(pager);
    }

    public static class BranchPagerAdapter extends FragmentPagerAdapter {
        private final Resources resources;
        private final File gitdir;
        private final Ref branch;

        public BranchPagerAdapter(FragmentManager fm, Resources resources, File gitdir, Ref branch) {
            super(fm);
            this.resources = resources;
            this.gitdir = gitdir;
            this.branch = branch;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return LogFragment.newInstance(gitdir, asList(branch.getName()), null);
                case 1:
                    return FileListFragment.newInstance(gitdir, branch.getName());
            }
            throw new RuntimeException("What " + position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return resources.getString(new int[] { R.string.commits, R.string.files }[position]);
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

    @Override
    public boolean onSearchRequested() { // Search key pressed.
        onSearchRequestedForCurrentFragment(pager);
        return true;
    }

    private Ref branch() {
        try {
            return repo().getRef(branchName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
