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

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import java.io.IOException;
import java.util.Map;

import android.app.Activity;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.TypefaceSpan;
import com.madgag.agit.diff.CommitChangeListAdapter;
import com.madgag.agit.diff.DiffSliderView;
import com.madgag.agit.views.ObjectIdView;
import com.madgag.agit.views.PersonIdentView;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import com.madgag.agit.CommitNavigationView.CommitSelectedListener;
import com.markupartist.android.widget.ActionBar;

public class CommitView extends LinearLayout {
	
	private static final String TAG = "CommitView";
    public static final CharacterStyle MONOSPACE_SPAN = new TypefaceSpan("monospace");

    private final LayoutInflater layoutInflater;
	private final TabHost tabHost;
	final ActionBar actionBar;
	private final TabWidget tabWidget;
	
	private CommitNavigationView commitNavigationView;
	
	private Repository repository;
	private PlotWalk revWalk;
	
	PlotCommit<PlotLane> commit;
	private Map<String, RevCommit> commitParents, commitChildren;

	private CommitSelectedListener commitSelectedListener;

	public CommitView(Context context, AttributeSet attrs) {
		super(context, attrs);	
		layoutInflater = LayoutInflater.from(context);
		
		layoutInflater.inflate(R.layout.commit_view, this);
		actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setHomeAction(new HomeAction((Activity)context));
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabWidget = (TabWidget) findViewById(android.R.id.tabs);
		tabHost.setup();
	}
	
	public void setRepositoryContext(Repository repository, PlotWalk revWalk) {
		this.repository = repository;
		this.revWalk = revWalk;
	}

    public void setCommit(final PlotCommit<PlotLane> c) throws MissingObjectException, IncorrectObjectTypeException, IOException {
        //this.commit = (PlotCommit<PlotLane>) revWalk.parseCommit(c);
        this.commit = c;
        Log.d(TAG, "setCommit : "+commit);
        Log.d(TAG, "actionBar : "+actionBar);
        SpannableStringBuilder title = new SpannableStringBuilder(commit.name().substring(0, 4)+" "+commit.getShortMessage());
        title.setSpan(MONOSPACE_SPAN,0,4,SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setTitle(title);

        Log.d(TAG, "About to clearAllTabs() on "+tabHost);
        tabHost.clearAllTabs();

        tabHost.addTab(detailTabSpec());

        showCommitDetailsFor(commit);

        commitParents = newHashMapWithExpectedSize(commit.getParentCount());
        TabContentFactory contentFactory = new TabContentFactory() {
            public View createTabContent(String tag) {
                RevCommit parentCommit = commitParents.get(tag);
                View v = layoutInflater.inflate(R.layout.rev_commit_view, tabWidget, false);
                DiffSliderView diffSlider = (DiffSliderView) v.findViewById(R.id.RevCommitDiffSlider);
                ExpandableListView expandableList = (ExpandableListView) v.findViewById(android.R.id.list);
                expandableList.setAdapter(new CommitChangeListAdapter(repository, commit, parentCommit, diffSlider, expandableList, getContext()));
                return v;
            }
        };


        for (RevCommit parentCommit : commit.getParents()) {
            parentCommit = revWalk.parseCommit(parentCommit);
            String parentId = parentCommit.getName();
            commitParents.put(parentId, parentCommit);
            TabSpec spec = tabHost.newTabSpec(parentId);
            String text = "Δ "+parentId.substring(0, 4);

            spec.setIndicator(newTabIndicator(tabHost, text)).setContent(contentFactory);
            tabHost.addTab(spec);
        }
        Log.d(TAG, "Added all tabs to "+tabHost);

        commitNavigationView.setCommit(commit);

    }

	private void showCommitDetailsFor(final PlotCommit<PlotLane> commit) {
		commitNavigationView = (CommitNavigationView) findViewById(R.id.commit_navigation);
		Log.d("CV", "Got commitNavigationView="+commitNavigationView+" commitSelectedListener="+commitSelectedListener);
		commitNavigationView.setCommitSelectedListener(commitSelectedListener);

        ((ObjectIdView)findViewById(R.id.commit_id)).setObjectId(commit);

		ViewGroup vg = (ViewGroup) findViewById(R.id.commit_people_group);
		
		PersonIdent author = commit.getAuthorIdent(), committer = commit.getCommitterIdent();
		if (author.equals(committer)) {
			addPerson("Author & Committer",author, vg);
		} else {
			addPerson("Author",author, vg);
			addPerson("Committer",committer, vg);
		}
//		ViewGroup vg = (ViewGroup) findViewById(R.id.commit_refs_group);
//		for (int i=0; i<commit.getRefCount(); ++i) {
//			TextView tv = new TextView(getContext());
//			tv.setText(commit.getRef(i).getName());
//			vg.addView(tv);
//		}
		TextView textView = (TextView) findViewById(R.id.commit_message_text);
		textView.setText(commit.getFullMessage());
//		
//		int width=textView.getBackground().getIntrinsicWidth(); // textView.getBackground is null at somepoint?
//		Log.d("CV", "M Width = "+width);
//		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, width/80);
	}

	private void addPerson(String title, PersonIdent commiter, ViewGroup vg) {
		PersonIdentView personIdentView = new PersonIdentView(getContext(), null);
		personIdentView.setIdent(title, commiter);
        LayoutParams layoutParams = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        layoutParams.weight=1;
		vg.addView(personIdentView,layoutParams);
	}

	private TabHost.TabSpec detailTabSpec() {
		TabHost.TabSpec spec;
		spec = tabHost.newTabSpec("commit_details")
			.setIndicator(newTabIndicator(tabHost, "Commit"))
			.setContent(new TabContentFactory() {
				public View createTabContent(String tag) {
					return layoutInflater.inflate(R.layout.commit_detail_view, tabHost.getTabWidget(), false);
				}
			});
		return spec;
	}
	
	public void setCommitSelectedListener(CommitSelectedListener commitSelectedListener) {
		this.commitSelectedListener = commitSelectedListener;
	}

	private void text(int textViewId, String text) {
		 TextView textView = (TextView) findViewById(textViewId);
		 textView.setText(text);
	}

	private TextView newTabIndicator(TabHost tabHost, String text) {
		TextView v=(TextView) layoutInflater.inflate(R.layout.tab_indicator, tabHost.getTabWidget(), false);
		v.setText(text);
		return v;
	}
	
}
