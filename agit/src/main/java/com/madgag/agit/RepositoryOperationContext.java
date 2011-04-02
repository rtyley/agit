package com.madgag.agit;

import static com.madgag.agit.RepositoryManagementActivity.manageRepoIntent;

import java.io.File;

import org.connectbot.service.PromptHelper;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.madgag.agit.operation.lifecycle.LongRunningServiceLifetime;
import com.madgag.agit.operation.lifecycle.RepoNotifications;
import com.madgag.agit.operations.GitAsyncTask;
import com.madgag.agit.operations.GitOperation;

public class RepositoryOperationContext {

	public static final String TAG = "RepositoryOperationContext";

	private final GitOperationsService service;
	private final File gitdir;
	private final RepoNotifications repoNotifications;
	private GitAsyncTask currentOperation;

	private final PromptHelper promptHelper;
	private ResponseProvider responseProvider;
	
	private Handler promptHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			responseProvider.accept(promptHelper);
		}
	};

	private RepositoryManagementActivity repositoryManagementActivity;

	public RepositoryOperationContext(File gitdir, GitOperationsService service, ResponseProvider responseProvider) {
		this.gitdir = gitdir.getAbsoluteFile();
		this.service = service;
		repoNotifications = new RepoNotifications(service, this.gitdir);
		promptHelper = new PromptHelper();
		this.responseProvider = responseProvider;
		promptHelper.setHandler(promptHandler);
	}

	public Service getService() {
		return service;
	}

	// grandiose name
	public void enqueue(GitOperation operation) {
		
		GitAsyncTask asyncTask = new GitAsyncTask(operation, new LongRunningServiceLifetime(repoNotifications, service));
		currentOperation = asyncTask;
		Log.d(TAG, "About to invoke "+asyncTask+" on "+gitdir+" ...");
		asyncTask.execute();
		Log.d(TAG, "...called execute!");
	}

	public Intent getRMAIntent() {
		return manageRepoIntent(gitdir);
	}

	public GitAsyncTask getCurrentOperation() {
		return currentOperation;
	}

	public ResponseInterface getResponseInterface() {
		return promptHelper;
	}
	
	public BlockingPromptService getBlockingPromptService() {
		return promptHelper;
	}

	public void setManagementActivity(RepositoryManagementActivity repositoryManagementActivity) {
		this.repositoryManagementActivity = repositoryManagementActivity;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + gitdir + "]";
	}



	public File getGitDir() {
		return gitdir;
	}

	public RepoNotifications getRepoNotifications() {
		return repoNotifications;
	}
}
