package com.madgag.agit;

import static com.madgag.agit.GitOperationsService.cloneOperationIntentFor;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;

import java.io.File;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.URIish;

import android.app.Notification;
import android.content.Intent;
import android.os.Environment;
import android.test.ServiceTestCase;
import android.util.Log;

public class GitOperationsServiceTest extends ServiceTestCase<GitOperationsService> {
	
	private static final String TAG="GitOperationsServiceTest";
	
	public GitOperationsServiceTest() {
		super(GitOperationsService.class);
	}
	
	public void testCanPerformSimpleReadOnlyCloneFromGitHub() throws Exception {
		URIish uri= new URIish("git://github.com/agittest/small-project.git");
		File gitdir=newFolder();
		Intent cloneIntent = cloneOperationIntentFor(uri, gitdir);
        cloneIntent.setClass(getContext(), GitOperationsService.class);
        
        Log.i(TAG, "About to start service with "+cloneIntent+" gitdir="+gitdir);
        startService(cloneIntent);
        
        RepositoryOperationContext repositoryOperationContext = getService().getOrCreateRepositoryOperationContextFor(gitdir);
		GitOperation gitOperation = waitForOperationIn(repositoryOperationContext);
		Notification notification = gitOperation.get();
        assertNotNull(notification);
        Repository repository = repositoryOperationContext.getRepository();
        Log.i(TAG, "After clone: repo directory="+repository.getDirectory()+" workTree="+repository.getWorkTree());
        assertTrue(repository.hasObject(ObjectId.fromString( "9e0b5e42b3e1c59bc83b55142a8c50dfae36b144")));
        assertFalse(repository.hasObject(ObjectId.fromString("111111111111111111111111111111111111cafe")));
	}
	
	
	private static GitOperation waitForOperationIn(RepositoryOperationContext context) {
		while (context.getCurrentOperation()==null) {
			try {
				sleep(100L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return context.getCurrentOperation();
	}
	
	private File newFolder() {
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		return new File(path, ""+currentTimeMillis());
	}
}
