package com.madgag.agit;

import static com.madgag.agit.GitTestUtils.newFolder;
import static org.eclipse.jgit.lib.Constants.DOT_GIT;

import java.io.File;

import android.content.Context;
import roboguice.test.RoboUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.madgag.agit.operation.lifecycle.RepoNotifications;

public class RepoNotificationsTest extends RoboUnitTestCase<AgitTestApplication> {
	
	private static final String TAG="RNT";
	
	@SmallTest
	public void testShouldHaveDifferentOngoingNotificationIds() throws Exception {
		File gitdir=new File(newFolder(), DOT_GIT);

        Context c = getInstrumentation().getContext();
        RepoNotifications roc1a = new RepoNotifications(c, gitdir);
		RepoNotifications roc1b = new RepoNotifications(c, gitdir);
		RepoNotifications roc2 = new RepoNotifications(c, gitdir);
		assertTrue(roc1a.getOngoingNotificationId()==roc1b.getOngoingNotificationId());
		assertFalse(roc1a.getOngoingNotificationId()==roc2.getOngoingNotificationId());
	}
}
