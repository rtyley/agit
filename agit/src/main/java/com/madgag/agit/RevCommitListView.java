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

import java.util.List;

import com.google.inject.Inject;
import com.madgag.agit.operation.lifecycle.CasualShortTermLifetime;
import com.madgag.agit.operations.Fetch;
import com.madgag.agit.operations.GitAsyncTaskFactory;
import com.madgag.agit.operations.OpNotification;
import com.markupartist.android.widget.PullToRefreshListView;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

import com.google.common.base.Function;
import roboguice.inject.InjectorProvider;

import static com.madgag.agit.Repos.remoteConfigFor;
import static org.eclipse.jgit.lib.Constants.DEFAULT_REMOTE_NAME;

public class RevCommitListView extends PullToRefreshListView {

    @Inject
    GitAsyncTaskFactory gitAsyncTaskFactory;
	private Function<RevCommit, Intent> commitViewerIntentCreator;
	
	public RevCommitListView(Context context, AttributeSet attrs) {
		super(context, attrs);
        ((InjectorProvider)context).getInjector().injectMembers(this);

		setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				RevCommit commit = (RevCommit) getAdapter().getItem(position);
				getContext().startActivity( commitViewerIntentCreator.apply(commit) );
			}
		});
	}


	public void setCommits(Function<RevCommit, Intent> commitViewerIntentCreator, final Repository repository, List<RevCommit> commits) {
		this.commitViewerIntentCreator = commitViewerIntentCreator;
		setAdapter(new RevCommitListAdapter(getContext(), commits));

        setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                Fetch fetch = new Fetch(repository, remoteConfigFor(repository, DEFAULT_REMOTE_NAME));
                gitAsyncTaskFactory.createTaskFor(fetch, new CasualShortTermLifetime() {
                    public void completed(OpNotification completionNotification) {
                        onRefreshComplete(completionNotification.getTickerText());
                    }
                }).execute();
            }
        });
        setFastScrollEnabled(true);
	}
}
