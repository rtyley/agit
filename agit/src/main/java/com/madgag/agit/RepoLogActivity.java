package com.madgag.agit;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.URIish;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RepoLogActivity extends ListActivity {
    private File gitdir;

    public static Intent repoLogIntentFor(File gitdir) {
		Intent intent = new Intent("git.LOG");
		intent
			.putExtra("gitdir", gitdir.getAbsolutePath());
		return intent;
	}
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.rev_commit_list);

        gitdir = GitIntents.gitDirFrom(getIntent());
        
        try {
			Repository repository=new FileRepository(gitdir);
			Iterable<RevCommit> commits = new Git(repository).log().call();
			
			setListAdapter(new RevCommitListAdapter(this, newArrayList(commits)));
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
    
	@Override
    protected void onResume() {
    	super.onResume();
        gitdir=RepositoryManagementActivity.getGitDirFrom(getIntent());
    }
}
