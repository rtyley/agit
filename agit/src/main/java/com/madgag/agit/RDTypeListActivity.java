package com.madgag.agit;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.eclipse.jgit.lib.Repository;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.markupartist.android.widget.ActionBar;
import roboguice.activity.RoboListActivity;

import static com.google.inject.name.Names.named;
import static com.madgag.agit.RepositoryActivity.enterRepositoryScopeFor;

public class RDTypeListActivity<E> extends RoboListActivity {
	
	public static Intent listIntent(Repository repository, String typeName) {
		return new GitIntentBuilder("git."+typeName+".LIST").repository(repository).toIntent();
	}
	
	private static final String TAG = "RDTL";
	private @Inject RepositoryContext rc;
    private @Inject Repository repository;
	private RepoDomainType<E> rdt;	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        RepositoryScope repositoryScope = enterRepositoryScopeFor(this,getIntent());
		try {
            super.onCreate(savedInstanceState);
            rdt = extractRDTFromIntent();
        } finally {
            repositoryScope.exit();
        }

		setContentView(R.layout.rdt_type_list);
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(rdt.conciseSummaryTitle());
		setListAdapter(new RDTypeListAdapter<E>(getLayoutInflater(), rdt));
		
        getListView().setEmptyView(findViewById(R.id.empty));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		startActivity(rdt.viewIntentFor((E)getListAdapter().getItem(position)));
	}
	
	private RepoDomainType<E> extractRDTFromIntent() {
		String rdtName = getIntent().getAction().split("\\.")[1];
        return getInjector().getInstance(Key.get(RepoDomainType.class, named(rdtName)));
//		if (rdtName.equals("remote")) {
//			return (RepoDomainType<E>) new RDTRemote(repository);
//		} else if (rdtName.equals("branch")) {
//			return (RepoDomainType<E>) new RDTBranch(repository);
//		}
//		return (RepoDomainType<E>) new RDTTag(repository);
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
