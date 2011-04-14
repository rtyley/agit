/*
 * Copyright (c) 2011 Roberto Tyley
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.madgag.agit;

import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;
import com.madgag.agit.operation.lifecycle.OperationLifecycleSupport;
import com.madgag.agit.operations.*;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.URIish;
import roboguice.test.RoboUnitTestCase;
import roboguice.util.RoboLooperThread;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static com.madgag.agit.GitTestUtils.integrationGitServerURIFor;
import static com.madgag.agit.GitTestUtils.newFolder;
import static com.madgag.agit.matchers.HasGitObjectMatcher.hasGitObject;
import static com.madgag.hamcrest.FileExistenceMatcher.exists;
import static com.madgag.hamcrest.FileLengthMatcher.ofLength;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

public class GitAsyncTaskTest extends RoboUnitTestCase<AgitTestApplication> {

	private static final String TAG = "GitAsyncTaskTest";
	
	@MediumTest
	public void testCloneRepoFromLocalTestServer() throws Exception {
		Clone cloneOp = new Clone(false, integrationGitServerURIFor("small-repo.early.git"), newFolder());
		
		Repository repo = executeAndWaitFor(cloneOp);
		
		assertThat(repo, hasGitObject("ba1f63e4430bff267d112b1e8afc1d6294db0ccc"));
        
        File readmeFile= new File(repo.getWorkTree(), "README");
        assertThat(readmeFile, exists());
        assertThat(readmeFile, ofLength(12));
	}

    @MediumTest
    public void testSimpleReadOnlyCloneFromGitHub() throws Exception {
        Clone cloneOp = new Clone(false, new URIish("git://github.com/agittest/small-project.git"), newFolder());
		Repository repo = executeAndWaitFor(cloneOp);

        assertThat(repo, hasGitObject("9e0b5e42b3e1c59bc83b55142a8c50dfae36b144"));
		assertThat(repo, not(hasGitObject("111111111111111111111111111111111111cafe")));

        File readmeFile= new File(repo.getWorkTree(), "README");
        assertThat(readmeFile, exists());
	}

	private Repository executeAndWaitFor(final GitOperation gitOperation)
			throws InterruptedException, IOException {
		final CountDownLatch latch = new CountDownLatch(1);
		new RoboLooperThread() {            
            public void run() {
            	GitAsyncTask task = injector.getInstance(GitAsyncTaskFactory.class).createTaskFor(gitOperation, new OperationLifecycleSupport() {
					public void startedWith(OpNotification ongoingNotification) {
                        Log.i(TAG,"Started "+gitOperation+" with "+ongoingNotification);
                    }
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
        return new FileRepository(gitOperation.getGitDir());
	}

}
