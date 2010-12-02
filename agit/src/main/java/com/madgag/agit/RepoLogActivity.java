package com.madgag.agit;

import static com.google.common.collect.Lists.newArrayList;
import static com.madgag.agit.GitIntents.addGitDirTo;
import static com.madgag.agit.GitIntents.gitDirFrom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Repository;
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
	
    private File gitdir;

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

        setGitDirFromIntent();
        
        try {
			setListAdapter(new RevCommitListAdapter(this, commitListForRepo()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		((ListView)findViewById(android.R.id.list)).setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				RevCommit commit = (RevCommit) ((RevCommitListAdapter) parent.getAdapter()).getItem(position);
				Toast.makeText(RepoLogActivity.this, commit.getName(), Toast.LENGTH_SHORT).show();
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.fromFile(gitdir),RepoLogActivity.this, RevCommitViewer.class);
				i.putExtra("commit", commit.name());
				RepoLogActivity.this.startActivity(i);
			}
		});
    }

	private List<RevCommit> commitListForRepo() throws IOException,
			NoHeadException {
		Repository repository=new FileRepository(gitdir);
		List<RevCommit> commits = newArrayList(new Git(repository).log().call());
		Log.d(TAG, "Found "+commits.size()+" commits");
		return commits;
	}

	@Override
    protected void onResume() {
    	super.onResume();
        setGitDirFromIntent();
        try {
        	Log.d(TAG, "Resuming... "+gitdir);
			((RevCommitListAdapter) getListAdapter()).updateWith(commitListForRepo());
		} catch (Exception e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
		}
    }
	
	
	private void setGitDirFromIntent() {
		gitdir = gitDirFrom(getIntent());
	}
    
}
