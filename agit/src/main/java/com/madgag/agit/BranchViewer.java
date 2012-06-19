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

package com.madgag.agit;

import static android.R.id.list;
import static android.text.format.DateUtils.FORMAT_SHOW_TIME;
import static android.text.format.DateUtils.formatDateTime;
import static android.widget.Toast.LENGTH_SHORT;
import static com.google.common.collect.Lists.newArrayList;
import static com.madgag.agit.CommitViewerActivity.commitViewerIntentCreatorFor;
import static com.madgag.agit.RDTypeListActivity.listIntent;
import static com.madgag.agit.git.Repos.niceNameFor;
import static com.madgag.android.ActionBarUtil.fixImageTilingOn;
import static com.madgag.android.ActionBarUtil.homewardsWith;
import static com.madgag.android.ActionBarUtil.setPrefixedTitleOn;
import static java.lang.System.currentTimeMillis;
import static org.eclipse.jgit.lib.Constants.DEFAULT_REMOTE_NAME;
import static org.eclipse.jgit.lib.Repository.shortenRefName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.madgag.agit.operation.lifecycle.CasualShortTermLifetime;
import com.madgag.agit.operations.Fetch;
import com.madgag.agit.operations.GitAsyncTaskFactory;
import com.madgag.agit.operations.OpNotification;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class BranchViewer extends RepoScopedActivityBase {

    public static Intent branchViewerIntentFor(File gitdir, Ref branch) {
        return new GitIntentBuilder("branch.VIEW").gitdir(gitdir).branch(branch).toIntent();
    }

    private final static int CHECKOUT_ID = Menu.FIRST;

    private static final String TAG = "BranchViewer";

    @InjectView(R.id.pull_to_refresh_list_wrapper)
    RevCommitListView revCommitListView;


    @Inject
    GitAsyncTaskFactory gitAsyncTaskFactory;
    @Inject
    Repository repository;
    @InjectExtra(value = "branch")
    String branchName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fixImageTilingOn(getSupportActionBar());
        setContentView(R.layout.branch_view);

        ActionBar actionBar = getSupportActionBar();
        setPrefixedTitleOn(actionBar, niceNameFor(repo()), shortenRefName(branch().getName()));
        actionBar.setDisplayHomeAsUpEnabled(true);
        setCommits();
        revCommitListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            public void onRefresh() {
                Fetch fetch = new Fetch(repository, DEFAULT_REMOTE_NAME);
                gitAsyncTaskFactory.createTaskFor(fetch, new CasualShortTermLifetime() {
                    public void error(OpNotification errorNotification) {
                        revCommitListView.setLastUpdatedLabel("Last Fetch failed: " + errorNotification.getTickerText());
                        revCommitListView.onRefreshComplete();
                        Toast.makeText(BranchViewer.this, errorNotification.getTickerText(), LENGTH_SHORT).show();
                    }

                    public void success(OpNotification completionNotification) {
                        setCommits();
                        revCommitListView.setLastUpdatedLabel("Last Fetch: " + formatDateTime(BranchViewer.this,
                                currentTimeMillis(), FORMAT_SHOW_TIME));
                        revCommitListView.onRefreshComplete();
                    }
                }).execute();
            }
        });
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
                return homewardsWith(this, listIntent(repository, "branch"));
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
            return repository.getRef(branchName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setCommits() {
        revCommitListView.setCommits(commitViewerIntentCreatorFor(repository.getDirectory(), branch()),
                commitListForRepo());
    }

    private List<RevCommit> commitListForRepo() {
        Git git = new Git(repository);
        try {
            Ref branch = branch();
            Log.d(TAG, "Calculating commitListForRepo based on " + branch + " branch.getObjectId()=" + branch
                    .getObjectId());
            Iterable<RevCommit> logWaa = git.log().add(branch.getObjectId()).call();
            List<RevCommit> sampleRevCommits = newArrayList(logWaa);

            Log.d(TAG, "Found " + sampleRevCommits.size() + " commits");

            return sampleRevCommits;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
