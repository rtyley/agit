package com.madgag.agit;

import static com.madgag.agit.GitOperationsService.cloneOperationIntentFor;
import static java.lang.Boolean.TRUE;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.eclipse.jgit.lib.Constants.DOT_GIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.URIish;

import roboguice.test.RoboServiceTestCase;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.madgag.agit.operations.GitAsyncTask;
import com.madgag.agit.operations.GitOperation;
import com.madgag.agit.operations.OpNotification;

public class GitOperationsServiceTest extends RoboServiceTestCase<GitOperationsService, AgitApplication> {

	private static final String TAG = "GitOperationsServiceTest";

	public GitOperationsServiceTest() {
		super(GitOperationsService.class);
	}
	
	private final ResponseProvider responseProvider = new ResponseProvider() {
		public void accept(ResponseInterface responseInterface) {
			Log.i("GOST.RP","Saying yes to "+responseInterface.getOpPrompt());
			responseInterface.setResponse(TRUE);
		}
	};

	public void testCanHitCloneRepoFromLocalTestServer() throws Exception {
		RepositoryOperationContext repositoryOperationContext = newRepoContext();
		
		Repository repository = clone(new URIish("ssh://" + gitServerHostAddress() + ":29418/path/to/repo.git"),repositoryOperationContext);
		assertTrue(repository.hasObject(ObjectId.fromString("9e0b5e42b3e1c59bc83b55142a8c50dfae36b144")));
	}

	public void testCanPerformSimpleReadOnlyCloneFromGitHub() throws Exception {
		RepositoryOperationContext repositoryOperationContext = newRepoContext();
		
		Repository repository = clone(new URIish("git://github.com/agittest/small-project.git"),repositoryOperationContext);
		File readme = new File(repository.getWorkTree(), "README");
		assertTrue(readme.exists());
		assertTrue(repository.hasObject(ObjectId.fromString("9e0b5e42b3e1c59bc83b55142a8c50dfae36b144")));
		assertFalse(repository.hasObject(ObjectId.fromString("111111111111111111111111111111111111cafe")));
	}

	private RepositoryOperationContext newRepoContext() {
		File gitdir = new File(newFolder(), DOT_GIT);
		GitOperationsService service = getService();
		Log.d(TAG, "newRepoContext() : service="+service); 
		return service.setRepositoryOperationContextFor(gitdir, responseProvider);
	}

	private Repository waitForPopulatedRepoIn(RepositoryOperationContext repositoryOperationContext) throws Exception {
		
		GitAsyncTask gitAsyncTask = waitForOperationIn(repositoryOperationContext);
		GitOperation operation = gitAsyncTask.getOperation();
		Log.i(TAG, "resultingRepoAt - operation=" + operation);
		
		//assertThat(repoInCurrentOp.getDirectory(), equalTo(gitdir));
		OpNotification opResult = gitAsyncTask.get(20, SECONDS);
		Log.i(TAG, "got opResult=" + opResult);
		assertNotNull(opResult);
		Repository repository = new FileRepository(repositoryOperationContext.getGitDir());
		Log.i(TAG, "After clone: repo directory=" + repository.getDirectory()
				+ " workTree=" + repository.getWorkTree());
		return repository;
	}
	
	private Repository clone(URIish sourceUri, RepositoryOperationContext repositoryOperationContext) throws Exception {
		startServiceCloning(sourceUri, repositoryOperationContext.getGitDir());
		return waitForPopulatedRepoIn(repositoryOperationContext);
	}

	private void startServiceCloning(URIish uri, File gitdir) {
		Intent cloneIntent = cloneOperationIntentFor(uri, gitdir);
		Log.i(TAG, "About to start service with " + cloneIntent + " gitdir="
				+ gitdir);
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

	private String gitServerHostAddress() throws IOException,
			FileNotFoundException, UnknownHostException {
		File bang = new File(Environment.getExternalStorageDirectory(),"agit-integration-test.properties");
		Properties properties = new Properties();
		properties.load(new FileReader(bang));
		String hostAddress = properties.getProperty("gitserver.host.address", "10.0.2.2");
		InetAddress address = InetAddress.getByName(hostAddress);
		boolean reachableHost = address.isReachable(1000);
		assertThat("Test gitserver host " + hostAddress + " is reachable", reachableHost, is(true));
		return hostAddress;
	}

	private static long unique_number = currentTimeMillis();

	public static File newFolder() {
		File path = new File(Environment.getExternalStorageDirectory(), "agit-test-repos");
		return new File(path, "" + (unique_number++));
	}
}
