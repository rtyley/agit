/*
 * Copyright (c) 2011 Roberto Tyley
 *
 * This file is part of 'Agit' - an Android Git client.
 *
 * Agit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Agit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.madgag.agit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.inject.Inject;
import com.madgag.agit.views.ObjectSummaryView;
import com.madgag.android.lazydrawables.ImageSession;
import com.markupartist.android.widget.ActionBar;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.RefUpdate.Result;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

import java.io.File;
import java.io.IOException;

import static com.madgag.agit.R.id.*;

public class TagViewer extends RepositoryActivity {

    public static Intent tagViewerIntentFor(File gitdir, String tagName) {
		return new GitIntentBuilder("git.tag.VIEW").gitdir(gitdir).tag(tagName).toIntent();
	}

    public static Intent tagViewerIntentFor(Repository repository, String tagName) {
		return new GitIntentBuilder("git.tag.VIEW").repository(repository).tag(tagName).toIntent();
	}

	private static final String TAG = "TV";

	private final static int DELETE_ID=Menu.FIRST;
	
	@Inject	private ImageSession avatarSession;
	
	@InjectView(actionbar) ActionBar actionBar;

	@InjectView(tv_tag_ref_object)
    ObjectSummaryView objectSummaryView;
	
	private RevTag revTag;

	private Ref tagRef;
    @InjectExtra(value="tag") String tagName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        repositoryScope.doWith(repo(), new Runnable() {
            public void run() {
                setContentView(R.layout.tag_viewer_activity);
            }
        });
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
    	tagRef = repo().getTags().get(tagName);
    	if (objectSummaryView==null) {
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
				// objectSummaryView.setObject(revWalk.parseAny(taggedId));

                final RevObject immediateTagRefObject = revWalk.parseAny(tagId);
                repositoryScope.doWith(repo(), new Runnable() {
                    public void run() {
                        objectSummaryView.setObject(immediateTagRefObject);
                    }
                });

                if (immediateTagRefObject instanceof RevTag) {
                    revTag = revWalk.parseTag(tagId);
                    actionBar.setTitle(revTag.getTagName());
                }

			} catch (IOException e) {
				Log.e(TAG, "Couldn't get parse tag", e);
				Toast.makeText(this, "Couldn't get tag "+tagId, Toast.LENGTH_LONG).show();
			}
		}
    }
}
