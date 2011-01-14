package com.madgag.agit;

import static com.madgag.agit.GitIntents.branchNameFrom;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.RefUpdate.Result;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class TagViewer extends android.app.Activity {

    public static Intent tagViewerIntentFor(Repository repository, String tagName) {
		return new GitIntentBuilder("git.view.TAG").repository(repository).tag(tagName).toIntent();
	}

	private static final String TAG = "TagViewer";

	private final static int DELETE_ID=Menu.FIRST;
	
    private Repository repository;
	
	private Ref tagRef;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tag_view);
		
		repository = GitIntents.repositoryFrom(getIntent());
		try {
			tagRef = repository.getRef(GitIntents.tagNameFrom(getIntent()));
		} catch (IOException e) {
			Log.e(TAG, "Couldn't get tag ref", e);
			throw new RuntimeException(e);
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, DELETE_ID, 0, R.string.delete_tag_menu_option).setShortcut('0', 'd');
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case DELETE_ID:
			try {
				RefUpdate update = repository.updateRef(tagRef.getName());
				// update.setNewObjectId(head);
				// update.setForceUpdate(force || remote);
				Result result = update.delete();
				Toast.makeText(this, result.name(), Toast.LENGTH_SHORT);
			} catch (IOException e) {
				e.printStackTrace();
			}
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		RepositoryCache.close(repository);
	}
}
