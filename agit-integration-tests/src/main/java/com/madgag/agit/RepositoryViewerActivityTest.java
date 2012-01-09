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

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.madgag.agit.git.model.RDTTag;
import com.madgag.agit.git.model.RDTTag.TagSummary;
import com.madgag.agit.matchers.GitTestHelper;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.util.List;

import static com.madgag.agit.RDTypeListActivity.listIntent;
import static com.madgag.agit.RepositoryViewerActivity.manageRepoIntent;
import static com.madgag.agit.matchers.CharSequenceMatcher.charSequence;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class RepositoryViewerActivityTest extends ActivityInstrumentationTestCase2<RepositoryViewerActivity> {

	private final static String TAG = RepositoryViewerActivityTest.class.getSimpleName();

	public RepositoryViewerActivityTest() {
		super("com.madgag.agit",RepositoryViewerActivity.class);
	}
	
	public void testShouldShowRepoViewerPageWithoutExplosion() throws Exception {

		GitTestHelper helper = AndroidTestEnvironment.helper(getInstrumentation());
		Repository repoWithTags = helper.unpackRepo("small-repo.with-tags.zip");
		
		setActivityIntent(manageRepoIntent(repoWithTags.getDirectory()));
		
		final RepositoryViewerActivity activity = getActivity(); // shouldn't crash

	}
	
}
