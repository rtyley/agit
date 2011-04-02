package com.madgag.agit;

import static com.madgag.agit.GitTestUtils.newFolder;
import static org.eclipse.jgit.lib.Constants.DOT_GIT;

import java.io.File;

import roboguice.test.RoboUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.madgag.agit.operation.lifecycle.RepoNotifications;

public class RepoNotificationsTest extends RoboUnitTestCase<AgitTestApplication> {
	
	private static final String TAG="RNT";
	
	@SmallTest
	public void testShouldHaveDifferentOngoingNotificationIds() throws Exception {
		File gitdir=new File(newFolder(), DOT_GIT);
		
		RepoNotifications roc1a = new RepoNotifications(null, gitdir);
		RepoNotifications roc1b = new RepoNotifications(null, gitdir);
		RepoNotifications roc2 = new RepoNotifications(null, gitdir);
		assertTrue(roc1a.getOngoingNotificationId()==roc1b.getOngoingNotificationId());
		assertFalse(roc1a.getOngoingNotificationId()==roc2.getOngoingNotificationId());
	}
}
