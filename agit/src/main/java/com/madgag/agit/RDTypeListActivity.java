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

import com.google.inject.Inject;
import com.google.inject.Key;
import com.madgag.agit.guice.RepositoryScope;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.madgag.android.listviews.ViewHolder;
import com.madgag.android.listviews.ViewHolderFactory;
import org.eclipse.jgit.lib.Repository;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.markupartist.android.widget.ActionBar;
import roboguice.activity.RoboListActivity;

import static android.R.layout.simple_list_item_2;
import static android.R.layout.two_line_list_item;
import static com.google.inject.name.Names.named;
import static com.madgag.agit.RepositoryActivity.enterRepositoryScopeFor;
import static com.madgag.android.listviews.ViewInflator.viewInflatorFor;

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

		setContentView(R.layout.list_activity_layout);
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(rdt.conciseSummaryTitle());
		setListAdapter(new ViewHoldingListAdapter<E>(rdt.getAll(), viewInflatorFor(this, two_line_list_item), new ViewHolderFactory<E>() {
            public ViewHolder<E> createViewHolderFor(View view) {
                return new RDTypeInstanceViewHolder(rdt,view);
            }
        }));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		startActivity(rdt.viewIntentFor((E)getListAdapter().getItem(position)));
	}
	
	private RepoDomainType<E> extractRDTFromIntent() {
		String rdtName = getIntent().getAction().split("\\.")[1];
        return getInjector().getInstance(Key.get(RepoDomainType.class, named(rdtName)));
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
