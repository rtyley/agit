package com.madgag.agit;

import static com.madgag.agit.CommitViewerActivity.revCommitViewIntentFor;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ObjectSummaryView extends LinearLayout {

	private TextView objectIdView;

	public ObjectSummaryView(Context context, AttributeSet attrs) {
		super(context, attrs);	
		LayoutInflater.from(context).inflate(R.layout.object_summary_view, this);
		
		objectIdView = (TextView) findViewById(R.id.osv_object_id_text);
	}
	
	public void setObject(final Repository repository, final RevObject revObject) {
		objectIdView.setText(revObject.getId().abbreviate(4).name());
		setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				getContext().startActivity(revCommitViewIntentFor(repository.getDirectory(), (RevCommit) revObject));
			}
		});
		int typeSpecificLayout = R.layout.commit_summary_view;
		View typeSpecificView = LayoutInflater.from(getContext()).inflate(typeSpecificLayout, this, false);
		RevCommit commit = (RevCommit) revObject;
		PersonIdentView personIdentView = (PersonIdentView) findViewById(R.id.csv_commit_author_ident);
		//personIdentView.setIdent(is, "Author", commit.getAuthorIdent());
	}
}
