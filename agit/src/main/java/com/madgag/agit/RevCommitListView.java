package com.madgag.agit;

import static com.madgag.agit.CommitViewer.revCommitViewIntentFor;

import java.util.List;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class RevCommitListView extends ListView {
		
	private Repository repo;
	
	public RevCommitListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFastScrollEnabled(true);
		setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				RevCommit commit = (RevCommit) getAdapter().getItem(position);
				getContext().startActivity(revCommitViewIntentFor(repo.getDirectory(), commit));
			}
		});
	}


	public void setCommits(Repository repo, List<RevCommit> commits) {
		this.repo = repo;
		setAdapter(new RevCommitListAdapter(getContext(), commits));
	}
}
