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

package com.madgag.agit.views;

import static com.madgag.agit.R.drawable.tag_icon;
import static com.madgag.agit.R.layout.simple_summary_list_item;
import static com.madgag.agit.RDTypeListActivity.listIntent;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.inject.Inject;
import com.madgag.agit.R;
import com.madgag.agit.git.model.RDTTag;

import org.eclipse.jgit.lib.Repository;

public class TagsSummaryView extends RelativeLayout implements EnabledListItem {
    private final TextView detail, title;
    private final Repository repository;
    private final RDTTag repoTags;

    @Inject
    public TagsSummaryView(Context context, LayoutInflater layoutInflater, Repository repository, RDTTag repoTags) {
        super(context);
        this.repository = repository;
        this.repoTags = repoTags;
        layoutInflater.inflate(simple_summary_list_item, this);

        ((ImageView) findViewById(R.id.rdt_icon)).setImageResource(tag_icon);
        title = (TextView) findViewById(R.id.title);
        detail = (TextView) findViewById(R.id.detail);

        updateStuff();
    }

    private void updateStuff() {
        title.setText(repoTags.conciseSummaryTitle());
        detail.setText(repoTags.summariseAll());
    }

    public void onItemClick() {
        getContext().startActivity(listIntent(repository, repoTags.name()));
    }


}
