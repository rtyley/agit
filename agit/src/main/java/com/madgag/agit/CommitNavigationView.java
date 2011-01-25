package com.madgag.agit;

import static com.google.common.collect.Maps.newEnumMap;
import static com.madgag.agit.Relation.CHILD;
import static com.madgag.agit.Relation.PARENT;

import java.util.Map;

import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotLane;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class CommitNavigationView extends LinearLayout {

	private final LayoutInflater layoutInflater;
	private final Map<Relation, ViewGroup> buttonGroups = newEnumMap(Relation.class);

	private CommitSelectedListener commitSelectedListener;

	public CommitNavigationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		layoutInflater = LayoutInflater.from(context);

		layoutInflater.inflate(R.layout.commit_navigation_view, this);
		buttonGroups.put(PARENT, (ViewGroup) findViewById(R.id.commit_parent_navigation));
		buttonGroups.put(CHILD, (ViewGroup) findViewById(R.id.commit_child_navigation));
	}

	public void setCommit(PlotCommit<PlotLane> commit) {
		addButtonsFor(commit, PARENT);
		addButtonsFor(commit, CHILD);
	}

	private void addButtonsFor(PlotCommit<PlotLane> commit, final Relation relation) {
		ViewGroup buttonGroup = buttonGroups.get(relation);
		buttonGroup.removeAllViews();
		View.OnClickListener clickListener = new View.OnClickListener() {
			@SuppressWarnings("unchecked")
			public void onClick(View v) {
				commitSelectedListener.onCommitSelected(relation, (PlotCommit<PlotLane>) v.getTag());
			}
		};
		
		for (PlotCommit<PlotLane> relatedCommit : relation.relationsOf(commit)) {
			Button button = (Button) layoutInflater.inflate(R.layout.related_commit_button, buttonGroup, false);
			button.setTag(relatedCommit);
			String abbrId = relatedCommit.getName().substring(0, 4);
			String buttonText = (relation==PARENT)?("« "+abbrId):(abbrId+" »");
			button.setText(buttonText);
			button.setOnClickListener(clickListener);
			buttonGroup.addView(button);
		}
	}

	public void setCommitSelectedListener(
			CommitSelectedListener commitSelectedListener) {
		this.commitSelectedListener = commitSelectedListener;
	}

	public interface CommitSelectedListener {
		void onCommitSelected(Relation relation, PlotCommit<PlotLane> commit);
	}
}
