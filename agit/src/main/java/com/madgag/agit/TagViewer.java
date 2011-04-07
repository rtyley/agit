package com.madgag.agit;

import static com.madgag.agit.GitIntents.tagNameFrom;

import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.RefUpdate.Result;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;

import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.inject.Inject;
import com.madgag.android.lazydrawables.ImageSession;
import com.markupartist.android.widget.ActionBar;

public class TagViewer extends RepositoryActivity {

    public static Intent tagViewerIntentFor(Repository repository, String tagName) {
		return new GitIntentBuilder("git.tag.VIEW").repository(repository).tag(tagName).toIntent();
	}

	private static final String TAG = "TV";

	private final static int DELETE_ID=Menu.FIRST;
	
	@Inject	private ImageSession avatarSession;
	
	@InjectView(R.id.actionbar)
	private ActionBar actionBar;
	
	@InjectView(R.id.tv_tag_tagger_ident)
	private PersonIdentView taggerIdentView;
	
	@InjectView(R.id.tv_tag_tagged_object)
	private ObjectSummaryView objectSummaryView;
	
	private RevTag revTag;

	private Ref tagRef;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        repositoryScope.doWith(repo(), new Runnable() {
            public void run() {
                setContentView(R.layout.tag_view);
            }
        });

		Log.d(TAG, "Roboguice THINGO "+actionBar+" "+taggerIdentView+" "+objectSummaryView);
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
    	if (taggerIdentView==null) {
    		return;
    	}
    	
		if (tagRef==null) {
			actionBar.setTitle("unknown tag");
		} else {
			ObjectId peeledObjectId = repo().peel(tagRef).getPeeledObjectId();
			ObjectId taggedId = peeledObjectId==null?tagRef.getObjectId():peeledObjectId;
			RevWalk revWalk = new RevWalk(repo());
			
			ObjectId tagId = tagRef.getObjectId();
			try {
				objectSummaryView.setObject(revWalk.parseAny(taggedId));
				revTag = revWalk.parseTag(tagId);
				actionBar.setTitle(revTag.getTagName());
				taggerIdentView.setIdent("Tagger", revTag.getTaggerIdent());
			} catch (IOException e) {
				Log.e(TAG, "Couldn't get parse tag", e);
				Toast.makeText(this, "Couldn't get tag "+tagId, Toast.LENGTH_LONG).show();
			}
		}
    }
}
