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

import static com.madgag.agit.AndroidTestEnvironment.helper;
import static com.madgag.agit.RDTypeListActivity.listIntent;
import android.widget.ListView;

import org.eclipse.jgit.lib.Repository;

public class RDTBranchListActivityTest extends RDTTypeListActivityStoryTestBase<RDTBranchListActivity> {

    // private final static String TAG = RDTBranchListActivityTest.class.getSimpleName();

    public RDTBranchListActivityTest() {
        super("com.madgag.agit", RDTBranchListActivity.class);
    }

    public void testShouldShowBranchListWithoutExplosion() throws Exception {
        Repository repository = helper(getInstrumentation()).unpackRepo("small-repo.with-branches.zip");
        setActivityIntent(listIntent(repository, "branch"));

        final RDTBranchListActivity activity = getActivity();

        ListView listView = activity.getListView();

        checkCanSelectEveryItemInNonEmpty(listView);
    }

}
