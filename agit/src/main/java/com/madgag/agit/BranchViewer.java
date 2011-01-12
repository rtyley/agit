package com.madgag.agit;

import static com.google.common.collect.Lists.newArrayList;
import static com.madgag.agit.GitIntents.addBranchTo;
import static com.madgag.agit.GitIntents.addGitDirTo;
import static com.madgag.agit.GitIntents.branchNameFrom;
import static com.madgag.agit.GitIntents.gitDirFrom;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class BranchViewer extends android.app.Activity {

    public static Intent branchViewerIntentFor(File gitdir, Ref branch) {
		Intent intent = new Intent("git.view.BRANCH");
		addGitDirTo(intent, gitdir);
		addBranchTo(intent, branch);
		return intent;
	}

	private static final String TAG = "BranchViewer";
	
	private Repository repository;
	private Ref branch;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.branch_view);
		Intent intent = getIntent();
		File gitdir = gitDirFrom(intent);
		try {
			repository = new FileRepository(gitdir);
			branch = repository.getRef(branchNameFrom(intent));
		} catch (IOException e) {
			Log.e(TAG, "Couldn't get branch ref", e);
			e.printStackTrace();
		}
		((ListView) findViewById(android.R.id.list)).setAdapter(new RevCommitListAdapter(this, commitListForRepo()));
	}

	private List<RevCommit> commitListForRepo() {
		Git git = new Git(repository);
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


}
