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

import static com.madgag.agit.CommitViewerActivity.revCommitViewIntentFor;
import static com.madgag.agit.OracleJVMTestEnvironment.helper;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(InjectedTestRunner.class)
public class CommitViewerRobolectricTest {
    private static File gitdirForSmallRepo;

    @BeforeClass
    public static void setUp() throws Exception {
        gitdirForSmallRepo = helper().unpackRepoAndGetGitDir("small-repo.with-tags.zip");
    }

    @Test
    public void shouldHaveCorrectCommitTitle() throws Exception {
        CommitViewerActivity activity = new CommitViewerActivity();
        activity.setIntent(revCommitViewIntentFor(gitdirForSmallRepo, "0d2489a0db53c5446ab3e8a93b91a18e061b25a9"));

//        activity.onCreate(null);
//        activity.onContentChanged();
//        assertThat(activity.currentCommitView.commit.getShortMessage(), equalTo("Adding my happy folder with it's
// tags"));


//        assertThat(activity.taggerIdentView.getIdent().getEmailAddress(), is("roberto.tyley@guardian.co.uk"));
//        assertThat(activity.tagMessage, visible());
//        assertThat(activity.tagMessage.getText().toString(), is("I even like the 2nd commit, I am tagging it\n"));
    }

}
