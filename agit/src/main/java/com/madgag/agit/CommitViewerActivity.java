/*
 * Copyright (c) 2011, 2012 Roberto Tyley
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
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

package com.madgag.agit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.animation.AnimationUtils.loadAnimation;
import static com.google.common.collect.Maps.newEnumMap;
import static com.madgag.agit.BranchViewer.branchViewerIntentFor;
import static com.madgag.agit.GitIntents.COMMIT;
import static com.madgag.agit.GitIntents.GITDIR;
import static com.madgag.agit.GitIntents.UNTIL_REVS;
import static com.madgag.agit.R.anim.pull_child_in;
import static com.madgag.agit.R.anim.pull_parent_in;
import static com.madgag.agit.R.anim.push_child_out;
import static com.madgag.agit.R.anim.push_parent_out;
import static com.madgag.agit.RepositoryViewerActivity.manageRepoIntent;
import static com.madgag.agit.git.model.Relation.CHILD;
import static com.madgag.agit.git.model.Relation.PARENT;
import static com.madgag.android.ActionBarUtil.fixImageTilingOn;
import static com.madgag.android.ActionBarUtil.homewardsWith;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.madgag.agit.CommitNavigationView.CommitSelectedListener;
import com.madgag.agit.git.model.Relation;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotCommitList;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;

public class CommitViewerActivity extends RepoScopedActivityBase {
    private static final String TAG = "CVA";

    public static GitIntentBuilder commitViewIntentFor(Bundle sourceArgs) {
        return new GitIntentBuilder("commit.VIEW", sourceArgs, GITDIR, UNTIL_REVS, COMMIT);
    }

    public static Intent commitViewIntentFor(Repository repository, RevCommit commit) {
        return new GitIntentBuilder("commit.VIEW").repository(repository).commit(commit).toIntent();
    }

    @Inject
    LogStartProvider logStartProvider;

    private PlotCommit<PlotLane> commit;

    CommitView currentCommitView, nextCommitView;

    private Map<Relation, RelationAnimations> relationAnimations = newEnumMap(Relation.class);

    private class RelationAnimations {
        private final Animation animateOldViewOut, animateNewViewIn;

        public RelationAnimations(int animateOldViewOutId, int animateNewViewInId) {
            animateOldViewOut = loadAnimation(CommitViewerActivity.this, animateOldViewOutId);
            animateNewViewIn = loadAnimation(CommitViewerActivity.this, animateNewViewInId);
        }

        void animateViews() {
            currentCommitView.startAnimation(animateOldViewOut);
            nextCommitView.startAnimation(animateNewViewIn);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fixImageTilingOn(getSupportActionBar());
        setContentView(R.layout.commit_navigation_animation_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            ObjectId revisionId = GitIntents.commitIdFrom(getIntent());
            Log.d(TAG, revisionId.getName());
            PlotWalk revWalk = generatePlotWalk();

            commit = (PlotCommit<PlotLane>) revWalk.parseCommit(revisionId);

            setup(currentCommitView, commitSelectedListener, revWalk);
            setup(nextCommitView, commitSelectedListener, revWalk);

            currentCommitView.setCommit(commit);
            setCurrentCommitViewVisible();
        } catch (Exception e) {
            Log.e(TAG, "Problem my friend", e);
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
        Stopwatch stopwatch = new Stopwatch().start();
        PlotWalk revWalk = new PlotWalk(repo());
        logStartProvider.markStartsOn(revWalk);

        PlotCommitList<PlotLane> plotCommitList = new PlotCommitList<PlotLane>();
        plotCommitList.source(revWalk);
        plotCommitList.fillTo(Integer.MAX_VALUE);
        Log.d(TAG, "generatePlotWalk duration" + stopwatch.stop());
        return revWalk;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                String ref = logStartProvider.getCurrentRef();
                Intent intent = (ref == null) ? manageRepoIntent(gitdir()) : branchViewerIntentFor(gitdir(), ref);
                return homewardsWith(this, intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
