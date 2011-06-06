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

import static com.madgag.agit.RepositoryViewerActivity.manageRepoIntent;
import static com.madgag.agit.RepositoryViewerActivity.manageRepoPendingIntent;

import java.io.File;

import com.madgag.agit.blockingprompt.PromptUIProvider;

import android.app.Service;
import android.content.Intent;

import com.madgag.agit.operation.lifecycle.RepoNotifications;
import com.madgag.agit.operations.GitAsyncTask;

public class RepositoryOperationContext {

	public static final String TAG = "RepositoryOperationContext";

	private final GitOperationsService service;
	private final File gitdir;
	private final RepoNotifications repoNotifications;
	private GitAsyncTask currentOperation;

	private PromptUIProvider promptUIProvider;

	private RepositoryViewerActivity repositoryViewerActivity;

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

	public void setManagementActivity(RepositoryViewerActivity repositoryViewerActivity) {
		this.repositoryViewerActivity = repositoryViewerActivity;
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
