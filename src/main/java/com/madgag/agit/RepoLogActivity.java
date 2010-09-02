package com.madgag.agit;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RepoLogActivity extends ListActivity {
    private File gitdir;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.rev_commit_list);
        
        // Query for all people contacts using the Contacts.People convenience class.
        // Put a managed wrapper around the retrieved cursor so we don't have to worry about
        // requerying or closing it as the activity changes state.
        Cursor mCursor = managedQuery(GitInfoProvider.CONTENT_URI, null, null, null, null);

        gitdir=RepositoryManagementActivity.getGitDirFrom(getIntent());
        
        try {
			Repository repository=new FileRepository(gitdir);
			Iterable<RevCommit> commits = new Git(repository).log().call();
			
			ListAdapter adapter = new RevCommitListAdapter(this, newArrayList(commits));

			// Bind to our new adapter.
			setListAdapter(adapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		((ListView)findViewById(android.R.id.list)).setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				RevCommit commit = (RevCommit) ((RevCommitListAdapter) parent.getAdapter()).getItem(position);
				Toast.makeText(RepoLogActivity.this, commit.getName(), Toast.LENGTH_SHORT).show();
			}
		});
    }
    
	@Override
    protected void onResume() {
    	super.onResume();
        gitdir=RepositoryManagementActivity.getGitDirFrom(getIntent());
    }
}
