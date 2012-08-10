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

import static com.madgag.agit.R.layout.branch_list_item;

import com.google.inject.Inject;
import com.madgag.agit.git.model.RDTBranch;
import com.madgag.agit.guice.ContextScopedViewInflatorFactory;
import com.madgag.android.listviews.ViewFactory;

import roboguice.inject.ContextSingleton;

@ContextSingleton
public class RDTBranchListActivity extends RDTypeListActivity<RDTBranch.BranchSummary> {

    private static final String TAG = "RDTBranchL";
    @Inject
    BranchViewHolderFactory viewHolderFactory;
    @Inject
    ContextScopedViewInflatorFactory inflatorFactory;

    @Override
    public ViewFactory<RDTBranch.BranchSummary> getViewFactory() {
        return new ViewFactory<RDTBranch.BranchSummary>(inflatorFactory.creatorFor(this, branch_list_item),
                viewHolderFactory);
    }
}
