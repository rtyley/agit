package com.madgag.agit;

import com.markupartist.android.widget.ActionBar;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class RDTypeListActivity<E> extends ListActivity {
	private static final String TAG = "RDTL";
	private RepositoryContext rc;
	private RepoDomainType<E> rdt;
	private View mEmptyView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rc = new RepositoryContext(this, TAG);
		rdt = extractRDTFromIntent();
		setContentView(R.layout.rdt_type_list);
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(rdt.conciseSummaryTitle());
		setListAdapter(new RDTypeListAdapter<E>(this, rdt));
		
		mEmptyView = findViewById(R.id.empty);
        getListView().setEmptyView(mEmptyView);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		startActivity(rdt.viewIntentFor((E)getListAdapter().getItem(position)));
	}
	
	private RepoDomainType<E> extractRDTFromIntent() {
		String rdtName = getIntent().getAction().split("\\.")[1];
		if (rdtName.equals("remote")) {
			return (RepoDomainType<E>) new RDTRemote(rc.repo());
		} else if (rdtName.equals("branch")) {
			return (RepoDomainType<E>) new RDTBranch(rc.repo());
		}
		return (RepoDomainType<E>) new RDTTag(rc.repo());
	}

	@Override
	protected void onResume() {
		super.onResume();
		rc.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		rc.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		rc.onDestroy();
	}
}
