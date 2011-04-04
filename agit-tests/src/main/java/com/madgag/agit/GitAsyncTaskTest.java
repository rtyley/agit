package com.madgag.agit;

import static com.madgag.agit.GitTestUtils.integrationGitServerURIFor;
import static com.madgag.agit.GitTestUtils.newFolder;
import static com.madgag.agit.HasGitObjectMatcher.hasGitObject;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import android.util.Log;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;

import roboguice.test.RoboUnitTestCase;
import roboguice.util.RoboLooperThread;
import android.test.suitebuilder.annotation.MediumTest;

import com.madgag.agit.operation.lifecycle.OperationLifecycleSupport;
import com.madgag.agit.operations.Clone;
import com.madgag.agit.operations.GitAsyncTask;
import com.madgag.agit.operations.GitOperation;
import com.madgag.agit.operations.OpNotification;

public class GitAsyncTaskTest extends RoboUnitTestCase<AgitTestApplication> {

	private static final String TAG = "GitAsyncTaskTest";
	
	@MediumTest
	public void testCanHitCloneRepoFromLocalTestServer() throws Exception {
		Clone cloneOp = new Clone(false, integrationGitServerURIFor("small-repo.early.git"), newFolder());
		
		executeAndWaitFor(cloneOp);
		
        Repository repo = new FileRepository(cloneOp.getGitDir());
		assertThat(repo, hasGitObject("ba1f63e4430bff267d112b1e8afc1d6294db0ccc"));
        
        File readmeFile= new File(repo.getDirectory(), "README");
        assertThat("File "+readmeFile+" exists", readmeFile.exists(), is(true));
        assertThat("File "+readmeFile+" length", readmeFile.length(), is(12L));
	}
	
	private void executeAndWaitFor(final GitOperation gitOperation)
			throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		new RoboLooperThread() {            
            public void run() {
            	GitAsyncTask task = injector.getInstance(GitAsyncTaskFactory.class).createTaskFor(gitOperation, new OperationLifecycleSupport() {
					public void startedWith(OpNotification ongoingNotification) {}
					public void publish(Progress progress) {}
					public void completed(OpNotification completionNotification) {
                        Log.i(TAG,"Completed "+gitOperation+" with "+completionNotification);
						latch.countDown();
					}
            	});
            	task.execute();
            }
        }.start();
        latch.await(20, SECONDS);
	}

}
