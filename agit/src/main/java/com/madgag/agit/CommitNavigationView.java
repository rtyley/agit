package com.madgag.agit;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static com.madgag.agit.Relation.CHILD;
import static com.madgag.agit.Relation.PARENT;

import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotLane;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class CommitNavigationView extends LinearLayout {
	
	private static final String TAG = "CNV";
	private final LayoutInflater layoutInflater;
	private final ViewGroup parentsButtonGroup, childrenButtonGroup;
	
	private PlotCommit<PlotLane> commit;
	private Map<String, PlotCommit<PlotLane>>
		commitParents = newHashMapWithExpectedSize(1), 
		commitChildren = newHashMapWithExpectedSize(1);

	public CommitNavigationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		layoutInflater = LayoutInflater.from(context);
		
		layoutInflater.inflate(R.layout.commit_navigation_view, this);
		parentsButtonGroup = (ViewGroup) findViewById(R.id.commit_parent_navigation);
		childrenButtonGroup = (ViewGroup) findViewById(R.id.commit_child_navigation);
	}
	
	public void setCommit(final PlotCommit<PlotLane> commit) {
			this.commit = commit;
			commitParents = PARENT.relationsOf(commit);
			commitChildren = CHILD.relationsOf(commit);
		    
		    addButtonsFor(parentsButtonGroup, commitParents.keySet(), PARENT);
		    addButtonsFor(childrenButtonGroup, commitChildren.keySet(), CHILD );
	}

	

	private void addButtonsFor(ViewGroup buttonGroup, Set<String> relatedCommits, Relation relation) {
		buttonGroup.removeAllViews();
		for (final String relatedCommit : relatedCommits) {
			Button button = (Button) layoutInflater.inflate(R.layout.related_commit_button, buttonGroup, false);
			button.setText(relatedCommit.substring(0, 4));
			button.setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v) {
	                 // startActivity(CommitViewer.revCommitViewIntentFor(gitdir(), relatedCommit));
	             }
	        });
			buttonGroup.addView(button);
		}
		Log.d(TAG, "Added to "+buttonGroup+" : "+relatedCommits);
	}
	
}
