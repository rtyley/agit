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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.animation.AnimationUtils.loadAnimation;
import static com.google.common.collect.Maps.newEnumMap;
import static com.madgag.agit.R.anim.*;
import static com.madgag.agit.R.string.tag_commit_menu_option;
import static com.madgag.agit.Relation.CHILD;
import static com.madgag.agit.Relation.PARENT;
import static java.lang.System.currentTimeMillis;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotCommitList;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.madgag.agit.CommitNavigationView.CommitSelectedListener;

public class CommitViewerActivity extends RepoScopedActivityBase {
	private static final String TAG = "CVA";
	
	private final static int TAG_ID=Menu.FIRST;
	
    public static Function<RevCommit, Intent> commitViewerIntentCreatorFor(final File gitdir, final Ref branch) {
		return new Function<RevCommit, Intent>() {
			public Intent apply(RevCommit commit) {
				return commitViewerIntentBuilderFor(gitdir).branch(branch).commit(commit).toIntent();
			}
		};
	}
    
    public static Intent revCommitViewIntentFor(File gitdir, String commitId) {
		return commitViewerIntentBuilderFor(gitdir).commit(commitId).toIntent();
	}
    
	private static GitIntentBuilder commitViewerIntentBuilderFor(File gitdir) {
		return new GitIntentBuilder("commit.VIEW").gitdir(gitdir);
	}

    @Inject LogStartProvider logStartProvider;

	private PlotCommit<PlotLane> commit;
	
	CommitView currentCommitView, nextCommitView;
	
	private Map<Relation,RelationAnimations> relationAnimations = newEnumMap(Relation.class);
	
	private class RelationAnimations {
		private final Animation animateOldViewOut,animateNewViewIn;
		
		public RelationAnimations(int animateOldViewOutId, int animateNewViewInId) {
			animateOldViewOut=loadAnimation(CommitViewerActivity.this,animateOldViewOutId);
			animateNewViewIn=loadAnimation(CommitViewerActivity.this,animateNewViewInId);
		}
		
		void animateViews() {
			currentCommitView.startAnimation(animateOldViewOut);
			nextCommitView.startAnimation(animateNewViewIn);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.commit_navigation_animation_layout);

		relationAnimations.put(PARENT, new RelationAnimations(push_child_out, pull_parent_in));
		relationAnimations.put(CHILD, new RelationAnimations(push_parent_out, pull_child_in));
		
		currentCommitView = (CommitView) findViewById(R.id.commit_nav_current_commit);
		nextCommitView = (CommitView) findViewById(R.id.commit_nav_next_commit);
		
		CommitSelectedListener commitSelectedListener = new CommitSelectedListener() {
			public void onCommitSelected(Relation relation, PlotCommit<PlotLane> commit) {
				setCommit(commit, relation);
			}
		};
		try {

			
			ObjectId revisionId = GitIntents.commitIdFrom(getIntent()); // intent.getStringExtra("commit");
			Log.d("RCCV", revisionId.getName());
			PlotWalk revWalk = generatePlotWalk();

			commit = (PlotCommit<PlotLane>) revWalk.parseCommit(revisionId);

			setup(currentCommitView, commitSelectedListener, revWalk);
			setup(nextCommitView, commitSelectedListener, revWalk);
			
			currentCommitView.setCommit(commit);
		    setCurrentCommitViewVisible();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setup(CommitView commitView,
			CommitSelectedListener commitSelectedListener,
			PlotWalk revWalk) {
		commitView.setRepositoryContext(repo(), revWalk);
		commitView.setCommitSelectedListener(commitSelectedListener);
	}

	

	private void setCurrentCommitViewVisible() {
		currentCommitView.setVisibility(VISIBLE);
		nextCommitView.setVisibility(GONE);
	}
	
	public void setCommit(PlotCommit<PlotLane> newCommit, Relation relation) {
		this.commit = newCommit;
		try {
			nextCommitView.setCommit(newCommit);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		relationAnimations.get(relation).animateViews();
		CommitView oldCurrent = currentCommitView;
		currentCommitView = nextCommitView;
		nextCommitView = oldCurrent;
		setCurrentCommitViewVisible();
	}

	private PlotWalk generatePlotWalk() throws IOException {
        long start=currentTimeMillis();
		PlotWalk revWalk = new PlotWalk(repo());

        for (ObjectId startId : logStartProvider.get()) {
            revWalk.markStart(revWalk.parseCommit(startId));
        }

		PlotCommitList<PlotLane> plotCommitList = new PlotCommitList<PlotLane>();
		plotCommitList.source(revWalk);
		plotCommitList.fillTo(Integer.MAX_VALUE);
        long duration=currentTimeMillis()-start;
        Log.d(TAG, "generatePlotWalk duration"+duration);
		return revWalk;
	}
	
	final int CREATE_TAG_DIALOG=0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        menu.add(0, TAG_ID, 0, tag_commit_menu_option).setShortcut('0', 't');
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case TAG_ID:
        	showDialog(CREATE_TAG_DIALOG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case CREATE_TAG_DIALOG:
            LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(R.layout.create_tag_dialog, null);
            return new AlertDialog.Builder(this)
//                .setIcon(R.drawable.alert_dialog_icon)
//                .setTitle(R.string.alert_dialog_text_entry)
                .setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String tagName=((TextView) textEntryView.findViewById(R.id.tag_name_edit)).getText().toString();
                        try {
                            new Git(repo()).tag().setName(tagName).setObjectId(commit).call();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .create();
        }
        return null;
    }

}
