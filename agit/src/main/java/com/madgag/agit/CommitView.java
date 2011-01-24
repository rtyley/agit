package com.madgag.agit;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabWidget;
import android.widget.TextView;

import com.markupartist.android.widget.ActionBar;

public class CommitView extends LinearLayout {
	
	private static final String TAG = "CommitView";

	private final LayoutInflater layoutInflater;
	private final TabHost tabHost;
	private final ActionBar actionBar;
	private final TabWidget tabWidget;
	private CommitChangeListAdapter mAdapter;
	
	private Repository repository;
	private PlotWalk revWalk;
	
	private PlotCommit<PlotLane> commit;
	private Map<String, RevCommit> commitParents, commitChildren;

	
	public CommitView(Context context, AttributeSet attrs) {
		super(context, attrs);
		layoutInflater = LayoutInflater.from(context);
		layoutInflater.inflate(R.layout.commit_view, this);
		
		actionBar = (ActionBar) findViewById(R.id.actionbar);
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabWidget = (TabWidget) findViewById(android.R.id.tabs);
		tabHost.setup();
	}
	
	public void setRepositoryContext(Repository repository, PlotWalk revWalk) {
		this.repository = repository;
		this.revWalk = revWalk;
	}
	
	public void setCommit(final PlotCommit<PlotLane> commit) throws MissingObjectException, IncorrectObjectTypeException, IOException {
			this.commit = commit;
			//Resources res = getResources(); // Resource object to get Drawables
			
			actionBar.setTitle(commit.name().substring(0, 4)+" "+commit.getShortMessage());
			
		    TabHost.TabSpec spec;
		    spec = tabHost.newTabSpec("commit_details")
		    	.setIndicator(newTabIndicator(tabHost, "Commit"))
		    	.setContent(R.id.content);
		    tabHost.addTab(spec);
		    
		    text(R.id.commit_id_text,commit.getName());
		    PersonIdent commiter = commit.getAuthorIdent(), author = commit.getCommitterIdent();
		    
		    text(R.id.commit_author_text,author.toExternalString());
			text(R.id.commit_commiter_text,commiter.toExternalString());

		    text(R.id.commit_message_text,commit.getFullMessage());
		    
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
		    	spec = tabHost.newTabSpec(parentId);
		    	String text = "Δ "+parentId.substring(0, 2);
		    	
				spec.setIndicator(newTabIndicator(tabHost, text)).setContent(contentFactory);
			    tabHost.addTab(spec);
		    }
		    
		    Map commitChildren = childrenOf(commit);
		    
		    addButtonsFor(R.id.commit_parent_navigation, commitParents.keySet());
		    addButtonsFor(R.id.commit_child_navigation, commitChildren.keySet());
		    
	}

	private Map<String,PlotCommit> childrenOf(final PlotCommit<PlotLane> commit) {
		Map<String,PlotCommit> commitChildren = newHashMapWithExpectedSize(commit.getChildCount());
		for (int i=0;i<commit.getChildCount();++i) {
			PlotCommit child = commit.getChild(i);
			commitChildren.put(child.getName(), child);
		}
		return commitChildren;
	}

	private void addButtonsFor(int navigation_view_id, Set<String> relatedCommits) {
		LinearLayout layout = (LinearLayout) findViewById(navigation_view_id);
		
		for (final String relatedCommit : relatedCommits) {
			Button button = (Button) layoutInflater.inflate(R.layout.related_commit_button, layout, false);
			layout.addView(button);
			button.setText(relatedCommit.substring(0, 4));
			button.setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v) {
	                 // startActivity(CommitViewer.revCommitViewIntentFor(gitdir(), relatedCommit));
	             }
	         });
		}
		Log.d("CV", "Added to "+navigation_view_id+" : "+relatedCommits);
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
