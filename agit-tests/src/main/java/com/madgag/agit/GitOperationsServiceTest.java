package com.madgag.agit;

import static com.madgag.agit.GitOperationsService.cloneOperationIntentFor;
import static com.madgag.agit.GitTestUtils.gitServerHostAddress;
import static com.madgag.agit.GitTestUtils.newFolder;
import static com.madgag.agit.HasGitObjectMatcher.hasGitObject;
import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.eclipse.jgit.lib.Constants.DOT_GIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

import java.io.File;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.URIish;

import roboguice.test.RoboServiceTestCase;
import android.content.Intent;
import android.util.Log;

import com.madgag.agit.operations.GitAsyncTask;
import com.madgag.agit.operations.GitOperation;

public class GitOperationsServiceTest extends RoboServiceTestCase<GitOperationsService, AgitTestApplication> {

	private static final String TAG = "GitOperationsServiceTest";
	
	public GitOperationsServiceTest() {
		super(GitOperationsService.class);
	}

	public void testCanHitCloneRepoFromLocalTestServer() throws Exception {
		Repository repository = clone(new URIish("ssh://" + gitServerHostAddress() + ":29418/sample-repo.git"));
		assertThat(repository, hasGitObject("155f7cca95943fab32ace9f056ce18089e160ec8"));
	}

	private Repository clone(URIish sourceUri) throws Exception {
		File gitdir = new File(newFolder(), DOT_GIT);
		startServiceCloning(sourceUri, gitdir);
//		RepositoryOperationContext roc = getService().getOrCreateRepositoryOperationContextFor(gitdir);
//		return waitForPopulatedRepoIn(roc);
		return new FileRepository(gitdir);
	}
	
	private Repository waitForPopulatedRepoIn(RepositoryOperationContext repositoryOperationContext) throws Exception {
		Log.i(TAG, "waitForPopulatedRepoIn=" + repositoryOperationContext);
		GitAsyncTask gitAsyncTask = waitForOperationIn(repositoryOperationContext);
		GitOperation operation = gitAsyncTask.getOperation();
		Log.i(TAG, "resultingRepoAt - operation=" + operation);
		
		//assertThat(repoInCurrentOp.getDirectory(), equalTo(gitdir));
		gitAsyncTask.getFutureInUse().get(20, SECONDS);
		Repository repository = new FileRepository(repositoryOperationContext.getGitDir());
		Log.i(TAG, "After clone: repo directory=" + repository.getDirectory()
				+ " workTree=" + repository.getWorkTree());
		return repository;
	}

	private void startServiceCloning(URIish uri, File gitdir) {
		Log.i(TAG, "startServiceCloning( uri="+uri+", gitdir="+gitdir+")");
		Intent cloneIntent = cloneOperationIntentFor(uri, gitdir);
		startService(cloneIntent);
	}
	
	// public void testCanShowAPromptToTheUser() throws Exception {
	// File gitdir=new File(newFolder(),DOT_GIT);
	// startService(new Intent());
	//
	// RepositoryOperationContext repositoryOperationContext =
	// getService().getOrCreateRepositoryOperationContextFor(gitdir);
	//
	// repositoryOperationContext.enqueue(new Action() {
	// public OpResult execute(RepositoryOperationContext
	// repositoryOperationContext, ProgressListener<Progress> progressListener)
	// {
	// repositoryOperationContext.getPromptHelper().requestBooleanPrompt("Can you see this",
	// "hint: say yes");
	// return new OpResult(stat_sys_warning, "Done", "", "");
	// }
	//
	// public int getOngoingIcon() {
	// return stat_sys_warning;
	// }
	//
	// public String getTickerText() {
	// return "Frond";
	// }
	// });
	//
	// this.getContext().startActivity(repositoryOperationContext.getRMAIntent());
	//
	//
	// }

	private static GitAsyncTask waitForOperationIn(
			RepositoryOperationContext context) {
		while (context.getCurrentOperation() == null) {
			try {
				sleep(100L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return context.getCurrentOperation();
	}
}
