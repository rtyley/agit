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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.inject.Inject;
import com.madgag.agit.operation.lifecycle.CasualShortTermLifetime;
import com.madgag.agit.operations.Fetch;
import com.madgag.agit.operations.GitAsyncTaskFactory;
import com.madgag.agit.operations.OpNotification;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.PullToRefreshListView;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.R.id.list;
import static com.google.common.collect.Lists.newArrayList;
import static com.madgag.agit.CommitViewerActivity.commitViewerIntentCreatorFor;
import static com.madgag.agit.Repos.remoteConfigFor;
import static org.eclipse.jgit.lib.Constants.DEFAULT_REMOTE_NAME;
import static org.eclipse.jgit.lib.Repository.shortenRefName;

public class BranchViewer extends RepositoryActivity {
    
    public static Intent branchViewerIntentFor(File gitdir, Ref branch) {
		return new GitIntentBuilder("git.branch.VIEW").gitdir(gitdir).branch(branch).toIntent();
	}

	private static final String TAG = "BranchViewer";
	
	@InjectView(R.id.actionbar) ActionBar actionBar;
	
	@InjectView(list) RevCommitListView revCommitListView;


    @Inject GitAsyncTaskFactory gitAsyncTaskFactory;
	@Inject Repository repository;
    @InjectExtra(value="branch") String branchName;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.branch_view);
		
		actionBar.setTitle(shortenRefName(branch().getName()));
        setCommits();
        revCommitListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
                Fetch fetch = new Fetch(repository, remoteConfigFor(repository, DEFAULT_REMOTE_NAME));
                gitAsyncTaskFactory.createTaskFor(fetch, new CasualShortTermLifetime() {
                    public void completed(OpNotification completionNotification) {
                        revCommitListView.onRefreshComplete(completionNotification.getTickerText());
                        setCommits();
                    }
                }).execute();
            }
        });
	}

    private Ref branch() {
        try {
            return repository.getRef(branchName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setCommits() {
        revCommitListView.setCommits(commitViewerIntentCreatorFor(repository.getDirectory(), branch()), commitListForRepo());
    }

    private List<RevCommit> commitListForRepo() {
		Git git = new Git(repository);
		try {
            Ref branch = branch();
            Log.d(TAG,"Calculating commitListForRepo based on "+ branch +" branch.getObjectId()="+ branch.getObjectId());
			Iterable<RevCommit> logWaa = git.log().add(branch.getObjectId()).call();
			List<RevCommit> sampleRevCommits = newArrayList(logWaa);
			
			Log.d(TAG, "Found "+sampleRevCommits.size()+" commits");

			return sampleRevCommits;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
