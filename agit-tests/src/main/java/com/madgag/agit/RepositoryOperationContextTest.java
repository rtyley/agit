package com.madgag.agit;


import static com.madgag.agit.GitOperationsService.cloneOperationIntentFor;
import static com.madgag.agit.GitOperationsServiceTest.newFolder;
import static org.eclipse.jgit.lib.Constants.DOT_GIT;

import java.io.File;

import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.URIish;

import android.content.Intent;
import android.test.ServiceTestCase;
import android.util.Log;

public class RepositoryOperationContextTest  extends ServiceTestCase<GitOperationsService> {
	
	private static final String TAG="RepositoryOperationContextTest";
	
	public RepositoryOperationContextTest() {
		super(GitOperationsService.class);
	}
	
	public void testShouldHavaeDifferentCompletionNotificationIds() throws Exception {
		File gitdir=new File(newFolder(),DOT_GIT);
		Intent cloneIntent = cloneOperationIntentFor(new URIish("git://github.com/agittest/small-project.git"), gitdir);
        Log.i(TAG, "About to start service with "+cloneIntent+" gitdir="+gitdir);
        startService(cloneIntent);

        FileRepository repo1 = new FileRepository("/tmp/poo1");
		RepositoryOperationContext roc1a = new RepositoryOperationContext(repo1, getService());
        RepositoryOperationContext roc1b = new RepositoryOperationContext(repo1, getService());
		RepositoryOperationContext roc2 = new RepositoryOperationContext(new FileRepository("/tmp/poo2"), getService());
		assertTrue(roc1a.opCompletionNotificationId==roc1b.opCompletionNotificationId);
		assertFalse(roc1a.opCompletionNotificationId==roc2.opCompletionNotificationId);
	}
}
