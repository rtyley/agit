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

import static org.eclipse.jgit.lib.Constants.DOT_GIT;
import android.content.Context;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.madgag.agit.matchers.GitTestHelper;
import com.madgag.agit.operation.lifecycle.RepoNotifications;

import java.io.File;

public class RepoNotificationsTest extends InstrumentationTestCase {

    private static final String TAG = "RNT";
    private final GitTestHelper helper = AndroidTestEnvironment.helper(getInstrumentation());

    @SmallTest
    public void testShouldHaveDifferentOngoingNotificationIds() throws Exception {
        File gitdir1 = new File(helper.newFolder(), DOT_GIT), gitdir2 = new File(helper.newFolder(), DOT_GIT);

        Context c = getInstrumentation().getContext();
        RepoNotifications roc1a = new RepoNotifications(c, gitdir1, null);
        RepoNotifications roc1b = new RepoNotifications(c, gitdir1, null);
        RepoNotifications roc2 = new RepoNotifications(c, gitdir2, null);
        assertTrue(roc1a.getOngoingNotificationId() == roc1b.getOngoingNotificationId());
        assertFalse(roc1a.getOngoingNotificationId() == roc2.getOngoingNotificationId());
    }
}
