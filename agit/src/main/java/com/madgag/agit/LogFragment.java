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

import static android.text.format.DateUtils.FORMAT_SHOW_TIME;
import static android.text.format.DateUtils.formatDateTime;
import static android.widget.Toast.LENGTH_SHORT;
import static com.google.common.collect.Lists.newArrayList;
import static com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import static com.madgag.agit.CommitViewerActivity.commitViewIntentFor;
import static com.madgag.agit.GitIntents.GITDIR;
import static com.madgag.agit.GitIntents.PATH;
import static com.madgag.agit.GitIntents.UNTIL_REVS;
import static com.madgag.android.listviews.ViewInflator.viewInflatorFor;
import static java.lang.System.currentTimeMillis;
import static org.eclipse.jgit.lib.Constants.DEFAULT_REMOTE_NAME;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.R;
import com.handmark.pulltorefresh.library.support.PullToRefreshListLoadingFragment;
import com.madgag.agit.operation.lifecycle.CasualShortTermLifetime;
import com.madgag.agit.operations.Fetch;
import com.madgag.agit.operations.GitAsyncTaskFactory;
import com.madgag.agit.operations.OpNotification;
import com.madgag.android.listviews.ViewHoldingListAdapter;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;

public class LogFragment extends PullToRefreshListLoadingFragment<RevCommit> {

    private static final String TAG = "LogFragment";

    public static LogFragment newInstance(File gitdir, List<String> untilRevs, @Nullable String path) {
        LogFragment f = new LogFragment();

        Bundle args = new Bundle();
        args.putString(GITDIR, gitdir.getAbsolutePath());
        args.putStringArrayList(UNTIL_REVS, newArrayList(untilRevs));
        args.putString(PATH, path);
        f.setArguments(args);

        return f;
    }

    @Inject
    GitAsyncTaskFactory gitAsyncTaskFactory;

    @Inject
    CommitViewHolderFactory commitViewHolderFactory;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final PullToRefreshListView pullToRefreshView = getPullToRefreshListView();
        pullToRefreshView.setShowIndicator(false);
        pullToRefreshView.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                try {
                    Fetch fetch = new Fetch(new FileRepository(getArguments().getString(GITDIR)), DEFAULT_REMOTE_NAME);
                    gitAsyncTaskFactory.createTaskFor(fetch, new CasualShortTermLifetime() {
                        public void error(OpNotification errorNotification) {
                            pullToRefreshView.setLastUpdatedLabel("Last Fetch failed: " + errorNotification.getTickerText());
                            pullToRefreshView.onRefreshComplete();
                            Toast.makeText(getActivity(), errorNotification.getTickerText(), LENGTH_SHORT).show();
                        }

                        public void success(OpNotification completionNotification) {
                            pullToRefreshView.setLastUpdatedLabel("Last Fetch: " + formatDateTime(getActivity(), currentTimeMillis(), FORMAT_SHOW_TIME));
                            refresh();
                        }
                    }).execute();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        getListView().setFastScrollEnabled(true);
    }

    @Override
    public Loader<List<RevCommit>> onCreateLoader(int id, Bundle args) {
        return new AsyncLoader<List<RevCommit>>(getActivity()) {
            public List<RevCommit> loadInBackground() {
                Stopwatch stopwatch = new Stopwatch().start();
                Bundle args = getArguments();
                try {
                    Repository repo = new FileRepository(args.getString(GITDIR));

                    LogCommand log = new Git(repo).log();
                    List<String> untilRevs = getArguments().getStringArrayList(UNTIL_REVS);
                    if (untilRevs==null || untilRevs.isEmpty()) {
                        log.all();
                    } else {
                        for (String untilRev : untilRevs) {
                            log.add(repo.resolve(untilRev));
                        }
                    }

                    List<RevCommit> sampleRevCommits = newArrayList(log.call());

                    Log.d(TAG, "Found " + sampleRevCommits.size() + " commits "+stopwatch);

                    return sampleRevCommits;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<RevCommit>> loader, List<RevCommit> items) {
        super.onLoadFinished(loader, items);
        getPullToRefreshListView().onRefreshComplete();
    }

    @Override
    protected ViewHoldingListAdapter<RevCommit> adapterFor(List<RevCommit> items) {
        return new ViewHoldingListAdapter<RevCommit>(items, viewInflatorFor(getActivity(), R.layout.rev_commit_list_item),
                commitViewHolderFactory);
    }

    @Override
    public void onListItemClick(ListView l, View v, int positionDisturbedByPullToRefresh, long id) {
        RevCommit commit = ((CommitViewHolder) v.getTag()).getCommit();
        startActivity(commitViewIntentFor(getArguments()).commit(commit).toIntent());
    }

}
