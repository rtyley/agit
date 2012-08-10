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

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.github.rtyley.android.screenshot.celebrity.Screenshots.poseForScreenshot;
import static com.madgag.agit.AndroidTestEnvironment.helper;
import static com.madgag.agit.TagViewer.tagViewerIntentFor;
import android.app.Activity;
import android.content.Intent;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import com.jayway.android.robotium.solo.Solo;

import java.io.File;

public class TagViewerActivityTest extends InstrumentationTestCase  {

    File gitdir;
    private Solo solo;

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation());
        gitdir = helper(getInstrumentation()).unpackRepoAndGetGitDir("small-repo.with-tags.zip");
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    @MediumTest
    public void testShouldShowAnnotatedTag() throws Exception {
        startActivitySync(tagViewerIntentFor(gitdir, "annotated-tag-of-2nd-commit"));
        solo.sleep(500);
        poseForScreenshot();
        assertTrue(solo.searchText("I even like the 2nd commit, I am tagging it"));
        assertTrue(solo.searchText("Adding my happy folder with it's tags"));
    }

    @MediumTest
    public void testShouldShowLightweightTag() throws Exception {
        startActivitySync(tagViewerIntentFor(gitdir, "lightweight-tag-of-2nd-commit"));
        solo.sleep(500);
        poseForScreenshot();
        assertTrue(solo.searchText("0d2489a0"));
    }

    private <T extends Activity> T startActivitySync(Intent intent) {
        intent.setFlags(intent.getFlags() | FLAG_ACTIVITY_NEW_TASK);
        return (T) getInstrumentation().startActivitySync(intent);
    }
}
