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

import static com.madgag.agit.AndroidTestEnvironment.helper;
import static com.madgag.agit.RepositoryViewerActivity.manageRepoIntent;
import android.test.ActivityInstrumentationTestCase2;

import org.eclipse.jgit.lib.Repository;

public class RepositoryViewerActivityTest extends ActivityInstrumentationTestCase2<RepositoryViewerActivity> {

    private final static String TAG = RepositoryViewerActivityTest.class.getSimpleName();

    public RepositoryViewerActivityTest() {
        super("com.madgag.agit", RepositoryViewerActivity.class);
    }

    public void testShouldShowRepoViewerPageWithoutExplosion() throws Exception {
        Repository repoWithTags = helper(getInstrumentation()).unpackRepo("small-repo.with-tags.zap");

        setActivityIntent(manageRepoIntent(repoWithTags.getDirectory()));

        getActivity(); // shouldn't crash
    }

}
