package com.madgag.agit;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.JGitInternalException;
import org.eclipse.jgit.api.NoHeadException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListAdapter;

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
		} catch (NoHeadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JGitInternalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	@Override
    protected void onResume() {
    	super.onResume();
        gitdir=RepositoryManagementActivity.getGitDirFrom(getIntent());
    }
}
