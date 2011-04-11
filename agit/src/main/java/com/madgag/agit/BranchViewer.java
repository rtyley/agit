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
import static com.google.common.collect.Lists.newArrayList;
import static com.madgag.agit.CommitViewerActivity.commitViewerIntentCreatorFor;
import static org.eclipse.jgit.lib.Repository.shortenRefName;

import java.io.File;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.markupartist.android.widget.ActionBar;

public class BranchViewer extends RepositoryActivity {
    
    public static Intent branchViewerIntentFor(File gitdir, Ref branch) {
		return new GitIntentBuilder("git.branch.VIEW").gitdir(gitdir).branch(branch).toIntent();
	}

	private static final String TAG = "BranchViewer";
	
	@InjectView(R.id.actionbar) ActionBar actionBar;
	
	@InjectView(list)
	private RevCommitListView revCommitListView;
	
	@Inject
	Repository repository;
	
	@Inject @Named("branch")
	Ref branch;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.branch_view);
		
		actionBar.setTitle(shortenRefName(branch.getName()));
		revCommitListView.setCommits(commitViewerIntentCreatorFor(repository.getDirectory(), branch),repository, commitListForRepo());
	}

	private List<RevCommit> commitListForRepo() {
		Git git = new Git(repository);
		try {
			Iterable<RevCommit> logWaa = git.log().add(branch.getObjectId()).call();
			List<RevCommit> sampleRevCommits = newArrayList(logWaa);
			
			Log.d(TAG, "Found "+sampleRevCommits.size()+" commits");

			return sampleRevCommits;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
