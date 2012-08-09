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

import static android.text.Html.fromHtml;
import static com.google.common.collect.Lists.newArrayList;
import static com.madgag.agit.BlobViewerActivity.revisionFileViewIntentFor;
import static com.madgag.agit.GitIntents.GITDIR;
import static com.madgag.agit.GitIntents.REVISION;
import static com.madgag.agit.git.Repos.shortenRevName;
import static com.madgag.android.HtmlStyleUtil.code;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.common.base.Stopwatch;
import com.madgag.agit.filepath.FilePath;
import com.madgag.agit.filepath.FilePathMatcher;
import com.madgag.agit.filepath.FilterableFileListAdapter;
import com.madgag.android.filterable.FilterWidgetSupport;
import com.madgag.android.filterable.OnSearchRequestedListener;
import com.madgag.android.listviews.ViewHoldingListAdapter;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.treewalk.TreeWalk;

/**
 * File list is specified by: 1 Repo, 1 Revision
 */
public class FileListFragment extends ListLoadingFragment<FilePath> implements Filterable, OnSearchRequestedListener {

    private static final String TAG = "FileListFragment";

    private FilterWidgetSupport filterWidgetSupport;

    public static FileListFragment newInstance(File gitdir, String revision) {
        FileListFragment f = new FileListFragment();

        Bundle args = new Bundle();
        args.putString(GITDIR, gitdir.getAbsolutePath());
        args.putString(REVISION, revision);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.file_list, menu);

        MenuItem item = menu.findItem(R.id.filter_files);
        filterWidgetSupport = new FilterWidgetSupport(item, this);
        String monospaceRevisionText = code(shortenRevName(getArguments().getString(REVISION)));
        filterWidgetSupport.setQueryHint(fromHtml(getString(R.string.filter_files_on_ref_hint, monospaceRevisionText)));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        FilePath filePath = (FilePath) getListAdapter().getItem(position);

        startActivity(revisionFileViewIntentFor(new File(getArguments().getString(GITDIR)), getArguments().getString
                (REVISION), filePath.getPath()));
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated with "+getArguments().getString(REVISION));
        getListView().setFastScrollEnabled(true);
    }

    @Override
    protected ViewHoldingListAdapter<FilePath> adapterFor(List<FilePath> items) {
        return new FilterableFileListAdapter(items, getActivity(), new AtomicReference<FilePathMatcher>());
    }

    @Override
    public Loader<List<FilePath>> onCreateLoader(int id, Bundle args) {
        return new AsyncLoader<List<FilePath>>(getActivity()) {
            public List<FilePath> loadInBackground() {

                try {
                    Bundle args = getArguments();
                    Repository repo = new FileRepository(args.getString(GITDIR));
                    RevCommit commit = new RevWalk(repo).parseCommit(repo.resolve(args.getString(REVISION)));

                    Stopwatch stopwatch = new Stopwatch().start();

                    final List<FilePath> paths = newArrayList();
                    TreeWalk treeWalk = new TreeWalk(repo);
                    treeWalk.setRecursive(true);
                    treeWalk.addTree(commit.getTree());

                    while (treeWalk.next()) {
                        paths.add(new FilePath(treeWalk.getRawPath()));
                    }
                    Log.d(TAG, "Found " + paths.size() + " files " + stopwatch.stop());

                    new Thread(new Runnable() {
                        @Override
                        public void run() { // knocks around 15-30% off time-to-display the list
                            Stopwatch stopwatch = new Stopwatch().start();
                            for (FilePath filePath : paths) { filePath.getPath(); }
                            Log.d(TAG, "Converted " + paths.size() + " path byte buffs to string " + stopwatch.stop());
                        }
                    }).start();
                    return paths;
                } catch (Exception e) {
                    Log.w(TAG, "Bang", e);
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Override
    public Filter getFilter() {
        ListAdapter listAdapter = getListAdapter();
        return listAdapter == null ? null : ((Filterable) listAdapter).getFilter();
    }

    public void onSearchRequested() {
        filterWidgetSupport.onSearchRequested();
    }
}
