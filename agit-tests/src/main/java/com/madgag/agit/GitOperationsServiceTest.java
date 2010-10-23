package com.madgag.agit;

import static com.madgag.agit.GitOperationsService.cloneOperationIntentFor;
import static java.lang.System.currentTimeMillis;
import static junit.framework.Assert.assertNotNull;

import java.io.File;

import junit.framework.Assert;

import org.eclipse.jgit.transport.URIish;

import android.app.Notification;
import android.content.Intent;
import android.os.Environment;
import android.test.ServiceTestCase;

public class GitOperationsServiceTest extends ServiceTestCase<GitOperationsService> {
	
	public GitOperationsServiceTest() {
		super(GitOperationsService.class);
	}
	
	public void testUsesDefaultGitDirLocationIfOnlySourceUriIsProvidedInIntent() throws Exception {
		URIish uri= new URIish("git://github.com/agittest/small-project.git");
		File gitdir=newFolder();
		Intent cloneIntent = cloneOperationIntentFor(uri, gitdir);
        cloneIntent.setClass(getContext(), GitOperationsService.class);
        
        startService(cloneIntent);
        
        Notification notification = getService().getOrCreateRepositoryOperationContextFor(gitdir).getCurrentOperation().get();
        assertNotNull(notification);
	}
	

	private File newFolder() {
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		return new File(path, ""+currentTimeMillis());
	}
}
