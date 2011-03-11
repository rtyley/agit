package com.madgag.agit;


import static com.madgag.agit.GitOperationsService.cloneOperationIntentFor;
import static com.madgag.agit.GitOperationsServiceTest.newFolder;
import static org.eclipse.jgit.lib.Constants.DOT_GIT;

import java.io.File;

import org.eclipse.jgit.transport.URIish;

import roboguice.test.RoboServiceTestCase;
import android.content.Intent;
import android.util.Log;

import com.madgag.agit.operation.lifecycle.RepoNotifications;

public class RepositoryOperationContextTest extends RoboServiceTestCase<GitOperationsService, AgitApplication> {
	
	private static final String TAG="RepositoryOperationContextTest";
	
	public RepositoryOperationContextTest() {
		super(GitOperationsService.class);
	}
	
	public void testShouldHaveDifferentOngoingNotificationIds() throws Exception {
		File gitdir=new File(newFolder(),DOT_GIT);
		Intent cloneIntent = cloneOperationIntentFor(new URIish("git://github.com/agittest/small-project.git"), gitdir);
        Log.i(TAG, "About to start service with "+cloneIntent+" gitdir="+gitdir);
        startService(cloneIntent);

        File repo1 = new File("/tmp/poo1");
		RepoNotifications roc1a = new RepositoryOperationContext(repo1, getService()).getRepoNotifications();
		RepoNotifications roc1b = new RepositoryOperationContext(repo1, getService()).getRepoNotifications();
		RepoNotifications roc2 = new RepositoryOperationContext(new File("/tmp/poo2"), getService()).getRepoNotifications();
		assertTrue(roc1a.getOngoingNotificationId()==roc1b.getOngoingNotificationId());
		assertFalse(roc1a.getOngoingNotificationId()==roc2.getOngoingNotificationId());
	}
}
