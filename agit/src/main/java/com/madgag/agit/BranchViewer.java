package com.madgag.agit;

import static com.google.common.collect.Lists.newArrayList;
import static com.madgag.agit.GitIntents.branchNameFrom;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class BranchViewer extends RepositoryActivity {
    
    public static Intent branchViewerIntentFor(File gitdir, Ref branch) {
		return new GitIntentBuilder("git.view.BRANCH").gitdir(gitdir).branch(branch).toIntent();
	}

	private static final String TAG = "BranchViewer";
	@Override String TAG() { return TAG; }
	
	private RevCommitListView revCommitListView;
	
	private Ref branch;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.branch_view);
		revCommitListView = (RevCommitListView) findViewById(android.R.id.list);
		
		try {
			branch = repo().getRef(branchNameFrom(getIntent()));
		} catch (IOException e) {
			Log.e(TAG, "Couldn't get branch ref", e);
			e.printStackTrace();
		}
		revCommitListView.setCommits(repo(), commitListForRepo());
	}

	private List<RevCommit> commitListForRepo() {
		Git git = new Git(repo());
		Iterable<RevCommit> logWaa;
		try {
			logWaa = git.log().add(branch.getObjectId()).call();
			List<RevCommit> sampleRevCommits = newArrayList(logWaa);
			
			Log.d(TAG, "Found "+sampleRevCommits.size()+" commits");

			return sampleRevCommits;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	void updateUI() {
		
	}
}
