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

package com.madgag.agit.operations;

import com.google.inject.Inject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import roboguice.inject.InjectResource;

import java.io.File;

import static android.R.drawable.stat_sys_download;
import static android.R.drawable.stat_sys_download_done;
import static com.madgag.agit.R.string.*;
import static com.madgag.agit.git.Repos.niceNameFor;
import static com.madgag.agit.operations.JGitAPIExceptions.exceptionWithFriendlyMessageFor;
import static org.eclipse.jgit.lib.Constants.DEFAULT_REMOTE_NAME;

public class Pull extends GitOperation {

	public static final String TAG = "Pull";
    private final Repository repo;

	@Inject Git git;
    @Inject MessagingProgressMonitor messagingProgressMonitor;
	@Inject CredentialsProvider credentialsProvider;
	@Inject	TransportConfigCallback transportConfigCallback;
	@InjectResource(pull) String opName;

    public Pull(Repository repository) {
        super(repository.getDirectory());
        this.repo = repository;
    }

	public int getOngoingIcon() {
		return stat_sys_download;
	}

	public OpNotification execute() {
		try {
			PullResult pullResult = git.pull().setProgressMonitor(messagingProgressMonitor)
				.setTransportConfigCallback(transportConfigCallback)
				.setCredentialsProvider(credentialsProvider)
				.call();
			return new OpNotification(stat_sys_download_done, str_operationCompleted(),
				string(pulled_from_remote, pullResult.getFetchedFrom()) +" - " + string(merge_status, pullResult.getMergeResult().getMergeStatus()));
		} catch (Exception e) {
			throw exceptionWithFriendlyMessageFor(e);
		}
    }

	public String getName() {
		return opName;
	}

	public CharSequence getUrl() {
		return ""; // TODO - give reasonable value
	}

	public String getTickerText() {
		return string(pulling)+"...";
	}

	public String getActionTitle() {
		return string(pulling_from_remote_on_repo, DEFAULT_REMOTE_NAME, niceNameFor(repo)); // TODO Correct remote name
	}

	public File getGitDir() {
		return repo.getDirectory();
	}
}
