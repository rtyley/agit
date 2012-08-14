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

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
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
import static com.madgag.agit.git.Repos.niceNameFor;
import static com.madgag.agit.git.model.Relation.CHILD;
import static com.madgag.agit.git.model.Relation.PARENT;
import static com.madgag.android.ActionBarUtil.fixImageTilingOn;
import static com.madgag.android.ActionBarUtil.homewardsWith;
import static com.madgag.android.ActionBarUtil.setPrefixedTitleOn;
import static com.madgag.android.ViewPagerUtil.onSearchRequestedForCurrentFragment;
import static org.eclipse.jgit.lib.Repository.shortenRefName;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.animation.Animation;

import com.actionbarsherlock.view.MenuItem;
import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.madgag.agit.CommitNavigationView.CommitSelectedListener;
import com.madgag.agit.git.model.Relation;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotCommitList;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;

public class CommitViewerActivity extends RepoScopedActivityBase implements CommitSelectedListener {
    private static final String TAG = "CVA";

    public static GitIntentBuilder commitViewIntentFor(Bundle sourceArgs) {
        return new GitIntentBuilder("commit.VIEW", sourceArgs, GITDIR, UNTIL_REVS, COMMIT);
    }

    public static Intent commitViewIntentFor(Repository repository, Ref ref) {
        return new GitIntentBuilder("commit.VIEW").repository(repository).untilRevs(ref).commit(ref.getObjectId()).toIntent();
    }


    public static Intent commitViewIntentFor(Repository repository, RevCommit commit) {
        return new GitIntentBuilder("commit.VIEW").repository(repository).commit(commit).toIntent();
    }

    @Inject
    LogStartProvider logStartProvider;

    private PlotWalk plotWalk;
    private PlotCommit<PlotLane> commit;

    CommitView currentCommitView, nextCommitView;

    public static final CharacterStyle MONOSPACE_SPAN = new TypefaceSpan("monospace");

    private Map<Relation, RelationAnimations> relationAnimations = newEnumMap(Relation.class);


    @Override
    public void onCommitSelected(Relation relation, PlotCommit<PlotLane> commit) {
        moveToCommit(commit, relation);
    }

    @Override
    public PlotCommit<PlotLane> plotCommitFor(ObjectId objectId) throws IOException {
        return (PlotCommit<PlotLane>) plotWalk.parseCommit(objectId);
    }

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

        try {
            ObjectId revisionId = GitIntents.commitIdFrom(getIntent());
            Log.d(TAG, revisionId.getName());

            plotWalk = generatePlotWalk();

            setCurrentCommit(plotCommitFor(revisionId));

            setup(currentCommitView, plotWalk);
            setup(nextCommitView, plotWalk);

            currentCommitView.setCommit(commit);
            setCurrentCommitViewVisible();
        } catch (Exception e) {
            Log.e(TAG, "Problem my friend", e);
        }
    }

    private void setup(CommitView commitView, PlotWalk revWalk) {
        commitView.setRepositoryContext(repo(), revWalk);
    }

    private void setCurrentCommitViewVisible() {
        currentCommitView.setVisibility(VISIBLE);
        nextCommitView.setVisibility(GONE);
    }

    public void moveToCommit(PlotCommit<PlotLane> newCommit, Relation relation) {
        setCurrentCommit(newCommit);

        try {
            nextCommitView.setCommit(newCommit);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        relationAnimations.get(relation).animateViews();
        swapCommitViewVars();
        setCurrentCommitViewVisible();
    }

    private void swapCommitViewVars() {
        CommitView oldCurrent = currentCommitView;
        currentCommitView = nextCommitView;
        nextCommitView = oldCurrent;
    }

    private void setCurrentCommit(PlotCommit<PlotLane> commit) {
        this.commit = commit;
        Log.d(TAG, "setCurrentCommit : commit=" + commit);
        setActionBarTitles();
    }

    private void setActionBarTitles() {
        SpannableStringBuilder prefixTitle = new SpannableStringBuilder(commit.name().substring(0, 4));
        prefixTitle.setSpan(MONOSPACE_SPAN, 0, 4, SPAN_EXCLUSIVE_EXCLUSIVE);
        String pathPrefix = niceNameFor(repo()) + " • ";
        String currentRef = logStartProvider.getCurrentRef();
        if (currentRef != null) {
            pathPrefix = pathPrefix + shortenRefName(currentRef) + " • ";
        }
        prefixTitle.insert(0, pathPrefix);
        setPrefixedTitleOn(getSupportActionBar(), prefixTitle, commit.getShortMessage());
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

    @Override
    public boolean onSearchRequested() { // Search key pressed.
        onSearchRequestedForCurrentFragment(currentCommitView.pager);
        return true;
    }
}
