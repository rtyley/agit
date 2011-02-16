package com.madgag.agit;

import static android.view.LayoutInflater.from;
import static com.madgag.agit.CommitViewerActivity.revCommitViewIntentFor;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;

import roboguice.inject.InjectView;
import roboguice.inject.InjectorProvider;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.inject.Inject;

public class ObjectSummaryView extends LinearLayout {

	@InjectView(R.id.osv_object_id_text) TextView objectIdView;
	
	@InjectView(R.id.osv_type_specific_data_frame) ViewGroup typeSpecificDataFrame;
	
	@Inject LayoutInflater layoutInflater;
	
	@Inject Repository repository;

	public ObjectSummaryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		((InjectorProvider)context).getInjector().injectMembers(this);
		setOrientation(VERTICAL);
		layoutInflater.inflate(R.layout.object_summary_view, this);
	}
	
	public void setObject( final RevObject revObject) {
		objectIdView.setText(revObject.getId().abbreviate(4).name());
		setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				getContext().startActivity(revCommitViewIntentFor(repository.getDirectory(), revObject.getName()));
			}
		});
		
		int typeSpecificLayout = R.layout.commit_summary_view;
		typeSpecificDataFrame.removeAllViews();
		View typeSpecificView = layoutInflater.inflate(typeSpecificLayout, typeSpecificDataFrame, false);
		typeSpecificDataFrame.addView(typeSpecificView);
		RevCommit commit = (RevCommit) revObject;
		PersonIdentView personIdentView = (PersonIdentView) typeSpecificView.findViewById(R.id.csv_commit_author_ident);
		personIdentView.setIdent("Author", commit.getAuthorIdent());
	}
}
