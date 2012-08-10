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


import static com.madgag.agit.BranchViewer.branchViewerIntentFor;
import static com.madgag.agit.CommitViewerActivity.commitViewIntentFor;
import static com.madgag.agit.GitIntents.PATH;
import static com.madgag.agit.GitIntents.REVISION;
import static com.madgag.agit.git.Repos.shortenRevName;
import static com.madgag.android.ActionBarUtil.fixImageTilingOn;
import static com.madgag.android.ActionBarUtil.homewardsWith;
import static com.madgag.android.ActionBarUtil.setPrefixedTitleOn;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.view.MenuItem;
import com.madgag.agit.git.Repos;

import java.io.File;

import org.eclipse.jgit.lib.AbbreviatedObjectId;

/**
 * File list is specified by: 1 Repo, 1 Revision, a full file path
 */
public class BlobViewerActivity extends RepoScopedActivityBase {
    private static final String TAG = "BlobViewerActivity";

    public static Intent revisionFileViewIntentFor(File gitdir, String revision, String path) {
        return new GitIntentBuilder("revision.file.VIEW").gitdir(gitdir).revision(revision).path(path).toIntent();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fixImageTilingOn(getSupportActionBar());

        Intent i = getIntent();
        String revision = i.getStringExtra(REVISION), path = i.getStringExtra(PATH);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // where this goes really depends on the context provided
        setActionBarTitleFor(revision, path);

        if (savedInstanceState == null) {
            BlobViewFragment details = BlobViewFragment.newInstance(gitdir(), revision, path);
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
        }
    }

    private void setActionBarTitleFor(String revision, String path) {
        String prefixTitle = Repos.niceNameFor(gitdir()) + " • " + shortenRevName(revision);

        int lastSlashIndex = path.lastIndexOf("/");
        String fileName;
        if (lastSlashIndex<0) {
            fileName = path;
        } else {
            fileName = path.substring(lastSlashIndex+1);
            prefixTitle += " • " +path.substring(0,lastSlashIndex);
        }

        setPrefixedTitleOn(getSupportActionBar(), prefixTitle, fileName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                String revision = getIntent().getStringExtra(REVISION);

                Intent intent = AbbreviatedObjectId.isId(revision)?commitViewIntentFor(getIntent().getExtras()).toIntent():branchViewerIntentFor(gitdir(), revision);
                return homewardsWith(this, intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
