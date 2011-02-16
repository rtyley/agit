package com.madgag.agit;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.common.base.Function;

public class RevCommitListView extends ListView {
		
	private Function<RevCommit, Intent> commitViewerIntentCreator;
	
	public RevCommitListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFastScrollEnabled(true);
		setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				RevCommit commit = (RevCommit) getAdapter().getItem(position);
				getContext().startActivity( commitViewerIntentCreator.apply(commit) );
			}
		});
	}


	public void setCommits(Function<RevCommit, Intent> commitViewerIntentCreator, List<RevCommit> commits) {
		this.commitViewerIntentCreator = commitViewerIntentCreator;
		setAdapter(new RevCommitListAdapter(getContext(), commits));
	}
}
