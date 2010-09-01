package com.madgag.agit;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.JGitInternalException;
import org.eclipse.jgit.api.NoHeadException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;

import com.google.common.collect.Lists;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

public class RepoLogActivity extends ListActivity {
    private File gitdir;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Query for all people contacts using the Contacts.People convenience class.
        // Put a managed wrapper around the retrieved cursor so we don't have to worry about
        // requerying or closing it as the activity changes state.
        Cursor mCursor = managedQuery(GitInfoProvider.CONTENT_URI, null, null, null, null);

        gitdir=RepositoryManagementActivity.getGitDirFrom(getIntent());
        
        try {
			Repository repository=new FileRepository(gitdir);
			Iterable<RevCommit> commits = new Git(repository).log().call();
			
			// Now create a new list adapter bound to the cursor.
			// SimpleListAdapter is designed for binding to a Cursor.
			ListAdapter adapter = new ArrayAdapter<RevCommit>(this, android.R.layout.simple_list_item_1, newArrayList(commits));

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
