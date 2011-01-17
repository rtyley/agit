package com.madgag.agit;

import static com.madgag.agit.GitIntents.tagNameFrom;

import java.io.IOException;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.RefUpdate.Result;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class TagViewer extends RepositoryActivity {

    public static Intent tagViewerIntentFor(Repository repository, String tagName) {
		return new GitIntentBuilder("git.tag.VIEW").repository(repository).tag(tagName).toIntent();
	}

	private static final String TAG = "TagViewer";
	
	@Override String TAG() { return TAG; }

	private final static int DELETE_ID=Menu.FIRST;
	
	private RevTag revTag;

	private Ref tagRef;

	private TextView titleTextView, taggerTextView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tag_view);
		titleTextView = (TextView) findViewById(R.id.tv_tag_viewer_title);
		taggerTextView = (TextView) findViewById(R.id.tv_tag_tagger);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        menu.add(0, DELETE_ID, 0, R.string.delete_tag_menu_option).setShortcut('0', 'd');
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Log.i(TAG, "onOptionsItemSelected "+item);
        switch (item.getItemId()) {
        case DELETE_ID:
			try {
				RefUpdate update = repo().updateRef(tagRef.getName());
				update.setForceUpdate(true);
				// update.setNewObjectId(head);
				// update.setForceUpdate(force || remote);
				Result result = update.delete();
				Toast.makeText(this, "Tag deletion : "+result.name(), Toast.LENGTH_SHORT).show();
				finish();
			} catch (IOException e) {
				Log.e(TAG, "Couldn't delete "+revTag.getName(), e);
				throw new RuntimeException(e);
			}
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onContentChanged() {
    	Log.d(TAG, "updateUI called");
    	tagRef = repo().getTags().get(tagNameFrom(getIntent()));	
    	if (titleTextView==null) {
    		return;
    	}
    	
		if (tagRef==null) {
			titleTextView.setText("unknown");
			taggerTextView.setText("");
		} else {
			try {
				revTag = new RevWalk(repo()).parseTag(tagRef.getObjectId());
				titleTextView.setText(revTag.getTagName());
				taggerTextView.setText(revTag.getTaggerIdent().getName());
			} catch (IOException e) {
				Log.e(TAG, "Couldn't get tag ref", e);
				throw new RuntimeException(e);
			}
		}
    }
}
