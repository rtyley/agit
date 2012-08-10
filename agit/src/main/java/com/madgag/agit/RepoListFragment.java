/*
 * Copyright (c) 2011, 2012 Roberto Tyley
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
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

package com.madgag.agit;

import static com.google.common.base.Functions.compose;
import static com.google.common.collect.Lists.transform;
import static com.madgag.agit.GitIntents.REPO_STATE_CHANGED_BROADCAST;
import static com.madgag.agit.GitIntents.actionWithSuffix;
import static com.madgag.agit.R.layout.repo_list_item;
import static com.madgag.agit.R.string.welcome_message;
import static com.madgag.agit.RepoSummary.REPO_SUMMARY_FOR_GITDIR;
import static com.madgag.agit.RepoSummary.sortReposByLatestCommit;
import static com.madgag.agit.RepositoryViewerActivity.manageRepoIntent;
import static com.madgag.agit.db.RepoRecord.GITDIR_FOR_RECORD;
import static com.madgag.android.ClickableText.makeLinksClickableIn;
import static com.madgag.android.listviews.ReflectiveHolderFactory.reflectiveFactoryFor;
import static com.madgag.android.listviews.ViewInflator.viewInflatorFor;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.madgag.agit.db.ReposDataSource;
import com.madgag.android.ClickableText;
import com.madgag.android.listviews.ViewHoldingListAdapter;

import java.util.List;

public class RepoListFragment extends ListLoadingFragment<RepoSummary> {

    private static final String TAG = "RepoListFragment";

    ReposDataSource reposDataSource;

    BroadcastReceiver repoStateChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "repoStateChangeReceiver got broadcast : " + intent);
            refresh();
        }
    };

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        reposDataSource = new ReposDataSource(activity);
    }

    @Override
    protected ViewHoldingListAdapter<RepoSummary> adapterFor(List<RepoSummary> items) {
        return new ViewHoldingListAdapter<RepoSummary>(items, viewInflatorFor(getActivity(), repo_list_item),
                reflectiveFactoryFor(RepositoryViewHolder.class));
    }

    @Override
    public Loader<List<RepoSummary>> onCreateLoader(int id, Bundle args) {
        return new AsyncLoader<List<RepoSummary>>(getActivity()) {
            public List<RepoSummary> loadInBackground() {
                return sortReposByLatestCommit(transform(reposDataSource.getAllRepos(),
                        compose(REPO_SUMMARY_FOR_GITDIR, GITDIR_FOR_RECORD)));
            }
        };
    }

    @Override
    public void onListItemClick(ListView list, View view, int position, long id) {
        RepoSummary repo = (RepoSummary) list.getItemAtPosition(position);
        startActivity(manageRepoIntent(repo.getRepo().getDirectory()));
    }

    public void onResume() {
        super.onResume();
        refresh();
        getActivity().registerReceiver(repoStateChangeReceiver,
                new IntentFilter(actionWithSuffix(REPO_STATE_CHANGED_BROADCAST)));
    }

    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(repoStateChangeReceiver);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setEmptyText(clickableWelcomeMessage());

        TextView emptyView = textViewForEmptyListText();
        makeLinksClickableIn(emptyView);
        emptyView.setPadding(20,20,20,20); // TODO Care about dip ratio for padding
    }

    private SpannableStringBuilder clickableWelcomeMessage() {
        SpannableStringBuilder message = new SpannableStringBuilder(getString(welcome_message));

        applyToEntireString(message, new TextAppearanceSpan(getActivity(), R.style.WelcomeText));
        final Context applicationContext = getActivity().getApplicationContext();
        CharacterStyle linkStyle = new ForegroundColorSpan(getResources().getColor(R.color.link_text));
        ClickableText.addLinks(message, linkStyle, new ClickableText.Listener() {
            public void onClick(String command, View widget) {
                if (command.equals("clone")) {
                    startActivity(new Intent(applicationContext, CloneLauncherActivity.class));
                }
            }
        });
        return message;
    }

    /*
     * Hackish, presumes empty view is a simple textview.
     */
    private TextView textViewForEmptyListText() {
        return (TextView) getListView().getEmptyView();
    }

    private void applyToEntireString(SpannableStringBuilder string, CharacterStyle style) {
        string.setSpan(style, 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

}
