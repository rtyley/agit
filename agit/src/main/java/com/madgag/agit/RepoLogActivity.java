package com.madgag.agit;

import static com.google.common.collect.Lists.newArrayList;
import static com.madgag.agit.GitIntents.addGitDirTo;
import static com.madgag.agit.GitIntents.gitDirFrom;
import static com.madgag.agit.RevCommitViewer.revCommitViewIntentFor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RepoLogActivity extends ListActivity {
	private static final String TAG = "RepoLogActivity";
	
    private Repository repository;

	private RevCommitListView revCommitListView;

    public static Intent repoLogIntentFor(Repository repository) {
		return repoLogIntentFor(repository.getDirectory());
	}
    
    public static Intent repoLogIntentFor(File gitdir) {
		Intent intent = new Intent("git.LOG");
		addGitDirTo(intent, gitdir);
		return intent;
	}
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.rev_commit_list);
        revCommitListView = (RevCommitListView) findViewById(android.R.id.list);
     
        updateListViewWithIntent();
    }


	private List<RevCommit> commitListForRepo() {
		try {
			List<RevCommit> commits = newArrayList(new Git(repository).log().call());
			Log.d(TAG, "Found "+commits.size()+" commits");
			return commits;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

	@Override
    protected void onResume() {
    	super.onResume();
    	updateListViewWithIntent();
    }

	private void updateListViewWithIntent() {
		repository = GitIntents.repositoryFrom(getIntent());
    	revCommitListView.setCommits(repository, commitListForRepo());
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		RepositoryCache.close(repository);
	}
	
}
