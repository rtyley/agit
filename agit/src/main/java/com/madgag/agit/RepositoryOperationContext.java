package com.madgag.agit;

import static com.madgag.agit.RepositoryManagementActivity.manageRepoIntent;
import static com.madgag.agit.RepositoryManagementActivity.manageRepoPendingIntent;

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

	private PromptUIProvider promptUIProvider;

	private RepositoryManagementActivity repositoryManagementActivity;

	public RepositoryOperationContext(File gitdir, GitOperationsService service, PromptUIProvider promptUIProvider) {
		this.gitdir = gitdir.getAbsoluteFile();
		this.service = service;
		repoNotifications = new RepoNotifications(service, this.gitdir, manageRepoPendingIntent(gitdir, service));
		this.promptUIProvider = promptUIProvider;
	}

	public Service getService() {
		return service;
	}

	public Intent getRMAIntent() {
		return manageRepoIntent(gitdir);
	}

	public GitAsyncTask getCurrentOperation() {
		return currentOperation;
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
