package com.madgag.agit;

import static com.madgag.agit.RepositoryManagementActivity.manageRepoIntent;
import static com.madgag.agit.RepositoryManagementActivity.manageRepoPendingIntent;

import java.io.File;

import org.connectbot.service.PromptHelper;
import org.eclipse.jgit.errors.NotSupportedException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.Transport;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jcraft.jsch.JSchException;
import com.madgag.agit.operation.lifecycle.LongRunningServiceLifetime;
import com.madgag.agit.operation.lifecycle.RepoNotifications;
import com.madgag.agit.operations.GitAsyncTask;
import com.madgag.agit.operations.GitOperation;
import com.madgag.agit.operations.OpNotification;
import com.madgag.agit.operations.OpPrompt;

public class RepositoryOperationContext implements ResponseProvider {

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
		repoNotifications = new RepoNotifications(service, this.gitdir, manageRepoPendingIntent(gitdir, service));
		promptHelper = new PromptHelper(TAG);
		this.responseProvider = responseProvider;
		promptHelper.setHandler(promptHandler);
	}

	public RepositoryOperationContext(File gitdir, GitOperationsService service) {
		this(gitdir, service, null);
		this.responseProvider = this;
	}

	public Service getService() {
		return service;
	}

	// grandiose name
	public void enqueue(GitOperation operation) {
		
		GitAsyncTask asyncTask = new GitAsyncTask(operation, new LongRunningServiceLifetime(repoNotifications, service, operation));
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

	public void accept(ResponseInterface responseInterface) {
		OpNotification opNotification = ((OpPrompt<?>) responseInterface.getOpPrompt()).getOpNotification();
		if (repositoryManagementActivity != null) {
			Log.i("I could prob show this directly without status bar",	opNotification.getEventDetail());
			repositoryManagementActivity.updateUIToReflectServicePromptRequests();
		} else {
			repoNotifications.notifyPromptWith(opNotification);
		}
	}

	public File getGitDir() {
		return gitdir;
	}

	public RepoNotifications getRepoNotifications() {
		return repoNotifications;
	}
}
